package com.ecommerce.flash.sale.service.impl;

import com.ecommerce.flash.sale.dto.ResCreateOrderDTO;
import com.ecommerce.flash.sale.dto.ResCreateOrderItemDTO;
import com.ecommerce.flash.sale.dto.ResCreateProductOrderItemDTO;
import com.ecommerce.flash.sale.entity.FlashSaleCampaign;
import com.ecommerce.flash.sale.entity.FlashSaleProduct;
import com.ecommerce.flash.sale.entity.UserPurchaseLimit;
import com.ecommerce.flash.sale.messaging.producer.FlashSaleProductProducer;
import com.ecommerce.flash.sale.messaging.producer.OrderFlashSaleProducer;
import com.ecommerce.flash.sale.repository.FlashSaleProductRepository;
import com.ecommerce.flash.sale.repository.UserPurchaseLimitRepository;
import com.ecommerce.flash.sale.service.OrderFlashSaleService;
import com.ecommerce.library.component.UserHelper;
import com.ecommerce.library.exception.HttpRequestException;
import com.ecommerce.library.kafka.event.flash.sale.FlashSaleOrderEvent;
import com.ecommerce.library.kafka.event.flash.sale.FlashSaleOrderItemEvent;
import com.ecommerce.library.kafka.event.flash.sale.FlashSaleProductEvent;
import com.ecommerce.library.kafka.event.flash.sale.FlashSaleProductOrderItemEvent;
import com.ecommerce.library.utils.MessageError;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class OrderFlashSaleServiceImpl implements OrderFlashSaleService {

    private final FlashSaleProductRepository flashSaleProductRepository;
    private final UserPurchaseLimitRepository userPurchaseLimitRepository;
    private final OrderFlashSaleProducer orderFlashSaleProducer;
    private final FlashSaleProductProducer flashSaleProductProducer;
    private final UserHelper userHelper;

    @Override
    @Transactional
    public void createOrder(ResCreateOrderDTO request) {
        Long userId = userHelper.getCurrentUserId();

        // Log incoming request summary
        log.info("createOrder called by userId={}", userId);
        if (request == null) {
            log.warn("createOrder received null request");
            return;
        }
        log.debug("Request receiverName={}, address={}, phoneNumber={}", request.getReceiverName(), request.getAddress(), request.getPhoneNumber());
        log.debug("Request items count={}", request.getItems() == null ? 0 : request.getItems().size());


        List<Long> flashSaleProductVariantIds = new ArrayList<>();
        Map<Long, Integer> requestedQuantityMap = new HashMap<>();
        Map<Long, BigDecimal> originalPriceMap = new HashMap<>();

        if (request.getItems() != null) {
            for (ResCreateOrderItemDTO item : request.getItems()) {
                log.debug("Processing order item: shopId={}, cartItemId={}", item.getShopId(), item.getCartItemId());
                if (item.getProductOrderItems() == null) {
                    log.debug("Order item has no productOrderItems, skipping");
                    continue;
                }
                for (ResCreateProductOrderItemDTO productOrderItem : item.getProductOrderItems()) {
                    log.debug("ProductOrderItem: productId={}, productVariantId={}, quantity={}, price={}, isFlashSale={}",
                            productOrderItem.getProductId(), productOrderItem.getProductVariantId(), productOrderItem.getQuantity(), productOrderItem.getPrice(), productOrderItem.getIsFlashSale());
                    if (Boolean.TRUE.equals(productOrderItem.getIsFlashSale())) {
                        flashSaleProductVariantIds.add(productOrderItem.getProductVariantId());
                        requestedQuantityMap.merge(productOrderItem.getProductVariantId(),
                                productOrderItem.getQuantity(), Integer::sum);
                        originalPriceMap.put(productOrderItem.getProductVariantId(), productOrderItem.getPrice());
                    }
                }
            }
        }
        log.info("Flash sale product variant IDs in order: {}", flashSaleProductVariantIds);
        log.debug("Requested quantity map: {}", requestedQuantityMap);
        log.debug("Original price map: {}", originalPriceMap);

        if (flashSaleProductVariantIds.isEmpty()) {
            log.info("No flash sale items in order, sending plain order event");
            sendOrderEvent(userId, request);
            return;
        }

        List<FlashSaleProduct> flashSaleProducts = flashSaleProductRepository
                .findActiveByProductVariantIdIn(flashSaleProductVariantIds, Instant.now());
        log.debug("FlashSaleProducts returned from repository count={}", flashSaleProducts == null ? 0 : flashSaleProducts.size());
        Map<Long, FlashSaleProduct> flashSaleProductMap = flashSaleProducts.stream()
                .collect(Collectors.toMap(FlashSaleProduct::getProductVariantId, fsp -> fsp));

        List<UserPurchaseLimit> userPurchaseLimits = userPurchaseLimitRepository
                .findByUserIdAndProductVariantIds(userId, flashSaleProductVariantIds, flashSaleProducts.get(0));
        log.debug("UserPurchaseLimits returned count={}", userPurchaseLimits == null ? 0 : userPurchaseLimits.size());
        Map<Long, UserPurchaseLimit> purchaseLimitMap = userPurchaseLimits.stream()
                .collect(Collectors.toMap(
                        upl -> upl.getFlashSaleProduct().getProductVariantId(),
                        upl -> upl
                ));
        log.info("User purchase limits map keys: {}", purchaseLimitMap.keySet());

        for (Long productVariantId : flashSaleProductVariantIds) {
            log.debug("Evaluating flash sale productVariantId={}", productVariantId);
            FlashSaleProduct flashSaleProduct = flashSaleProductMap.get(productVariantId);

            if (flashSaleProduct == null) {
                log.warn("No active FlashSaleProduct found for variantId={}, skipping", productVariantId);
                continue;
            }

            log.debug("FlashSaleProduct details: flashSaleProductId={}, flashSaleCampaignId={}, totalQuantity={}, soldQuantity={}, isSoldOut={}, maxQuantityPerUser={}",
                    flashSaleProduct.getFlashSaleProductId(),
                    flashSaleProduct.getFlashSaleCampaign() != null ? flashSaleProduct.getFlashSaleCampaign().getFlashSaleCampaignId() : null,
                    flashSaleProduct.getTotalQuantity(),
                    flashSaleProduct.getSoldQuantity(),
                    flashSaleProduct.getIsSoldOut(),
                    flashSaleProduct.getMaxQuantityPerUser());

            if (Boolean.TRUE.equals(flashSaleProduct.getIsSoldOut())) {
                log.error("Flash sale product variant {} is sold out", productVariantId);
                throw new HttpRequestException(
                        MessageError.FLASH_SALE_PRODUCT_SOLD_OUT,
                        400,
                        Instant.now()
                );
            }

            Integer requestedQuantity = requestedQuantityMap.get(productVariantId);
            log.debug("Requested quantity for variant {} = {}", productVariantId, requestedQuantity);

            int remainingStock = flashSaleProduct.getTotalQuantity() - flashSaleProduct.getSoldQuantity();
            log.debug("Remaining stock for variant {} = {}", productVariantId, remainingStock);
            if (requestedQuantity > remainingStock) {
                log.error("Requested quantity {} exceeds remaining stock {} for variant {}", requestedQuantity, remainingStock, productVariantId);
                throw new HttpRequestException(
                        MessageError.FLASH_SALE_TOTAL_QUANTITY_EXCEEDED,
                        400,
                        Instant.now()
                );
            }

            UserPurchaseLimit userPurchaseLimit = purchaseLimitMap.get(productVariantId);
            int alreadyPurchased = userPurchaseLimit != null ? userPurchaseLimit.getPurchasedQuantity() : 0;
            int maxQuantityPerUser = flashSaleProduct.getMaxQuantityPerUser();
            log.debug("UserPurchaseLimit for variant {}: alreadyPurchased={}, maxPerUser={}", productVariantId, alreadyPurchased, maxQuantityPerUser);

            if (alreadyPurchased + requestedQuantity > maxQuantityPerUser) {
                log.error("User purchase limit exceeded for userId={}, variantId={}, alreadyPurchased={}, requested={}, maxPerUser={}", userId, productVariantId, alreadyPurchased, requestedQuantity, maxQuantityPerUser);
                throw new HttpRequestException(
                        MessageError.FLASH_SALE_PURCHASE_LIMIT_EXCEEDED,
                        400,
                        Instant.now()
                );
            }

            // Update soldQuantity
            int oldSold = flashSaleProduct.getSoldQuantity();
            flashSaleProduct.setSoldQuantity(flashSaleProduct.getSoldQuantity() + requestedQuantity);
            log.debug("Updating soldQuantity for variant {}: {} -> {}", productVariantId, oldSold, flashSaleProduct.getSoldQuantity());
            if (flashSaleProduct.getSoldQuantity() >= flashSaleProduct.getTotalQuantity()) {
                flashSaleProduct.setIsSoldOut(true);
                log.info("Flash sale product variant {} marked as sold out", productVariantId);
            }
            flashSaleProductRepository.save(flashSaleProduct);
            log.debug("Saved FlashSaleProduct id={} to repository", flashSaleProduct.getFlashSaleProductId());

            FlashSaleCampaign campaign = flashSaleProduct.getFlashSaleCampaign();
            FlashSaleProductEvent productEvent = FlashSaleProductEvent.builder()
                    .flashSaleCampaignId(campaign.getFlashSaleCampaignId())
                    .flashSaleProductId(flashSaleProduct.getFlashSaleProductId())
                    .ownerId(flashSaleProduct.getOwnerId())
                    .shopId(flashSaleProduct.getShopId())
                    .productId(flashSaleProduct.getProductId())
                    .productVariantId(flashSaleProduct.getProductVariantId())
                    .originalPrice(originalPriceMap.get(productVariantId))
                    .discountPercentage(flashSaleProduct.getDiscountPercentage())
                    .totalQuantity(flashSaleProduct.getTotalQuantity())
                    .soldQuantity(flashSaleProduct.getSoldQuantity())
                    .maxQuantityPerUser(flashSaleProduct.getMaxQuantityPerUser())
                    .isSoldOut(flashSaleProduct.getIsSoldOut())
                    .score(flashSaleProduct.getScore())
                    .flashSaleCampaignName(campaign.getCampaignName())
                    .startTime(campaign.getStartTime())
                    .endTime(campaign.getEndTime())
                    .build();

            log.info("Sending FlashSaleProductEvent for variant {}: {}", productVariantId, productEvent);
            flashSaleProductProducer.sendUpdate(productEvent);

            // Create or update UserPurchaseLimit
            if (userPurchaseLimit != null) {
                int oldPurchased = userPurchaseLimit.getPurchasedQuantity();
                userPurchaseLimit.setPurchasedQuantity(alreadyPurchased + requestedQuantity);
                userPurchaseLimitRepository.save(userPurchaseLimit);
                log.debug("Updated UserPurchaseLimit for userId={}, variantId={}: {} -> {}", userId, productVariantId, oldPurchased, userPurchaseLimit.getPurchasedQuantity());
            } else {
                UserPurchaseLimit newUserPurchaseLimit = UserPurchaseLimit.builder()
                        .userId(userId)
                        .flashSaleProduct(flashSaleProduct)
                        .purchasedQuantity(requestedQuantity)
                        .build();
                userPurchaseLimitRepository.save(newUserPurchaseLimit);
                log.debug("Created UserPurchaseLimit for userId={}, variantId={}, purchasedQuantity={}", userId, productVariantId, requestedQuantity);
            }
        }

        log.info("All flash sale product processing done, sending order event");
        sendOrderEvent(userId, request);
    }

    private void sendOrderEvent(Long userId, ResCreateOrderDTO request) {
        FlashSaleOrderEvent event = FlashSaleOrderEvent.builder()
                .userId(userId)
                .receiverName(request.getReceiverName())
                .address(request.getAddress())
                .phoneNumber(request.getPhoneNumber())
                .items(request.getItems().stream()
                        .map(item -> FlashSaleOrderItemEvent.builder()
                                .shopId(item.getShopId())
                                .cartItemId(item.getCartItemId())
                                .note(item.getNote())
                                .productOrderItems(item.getProductOrderItems().stream()
                                        .map(poi -> FlashSaleProductOrderItemEvent.builder()
                                                .productId(poi.getProductId())
                                                .discount(poi.getDiscount())
                                                .productVariantId(poi.getProductVariantId())
                                                .quantity(poi.getQuantity())
                                                .price(poi.getPrice())
                                                .isFlashSale(poi.getIsFlashSale())
                                                .build())
                                        .toList())
                                .build())
                        .toList())
                .build();

        log.info("Sending FlashSaleOrderEvent for userId={}: {}", userId, event);
        orderFlashSaleProducer.send(event);
    }
}
