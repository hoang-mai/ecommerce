package com.ecommerce.flash.sale.service.impl;

import com.ecommerce.flash.sale.entity.FlashSaleProduct;
import com.ecommerce.flash.sale.entity.UserPurchaseLimit;

import com.ecommerce.flash.sale.messaging.producer.OrderFlashSaleProducer;
import com.ecommerce.flash.sale.repository.FlashSaleProductRepository;
import com.ecommerce.flash.sale.repository.UserPurchaseLimitRepository;
import com.ecommerce.flash.sale.service.OrderFlashSaleService;
import com.ecommerce.library.component.UserHelper;
import com.ecommerce.library.enumeration.OrderStatus;
import com.ecommerce.library.exception.NotFoundException;
import com.ecommerce.library.kafka.event.flashsale.RestoreFlashSaleStockEvent;
import com.ecommerce.library.kafka.event.order.CreateListOrderEvent;
import com.ecommerce.library.utils.FnCommon;
import com.ecommerce.library.utils.MessageError;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@RequiredArgsConstructor
@Service
public class OrderFlashSaleServiceImpl implements OrderFlashSaleService {

    private final FlashSaleProductRepository flashSaleProductRepository;
    private final UserPurchaseLimitRepository userPurchaseLimitRepository;
    private final OrderFlashSaleProducer orderFlashSaleProducer;

    @Transactional
    @Override
    public void processFlashSaleOrder(CreateListOrderEvent createListOrderEvent) {
        Long userId = createListOrderEvent.getUserId();
        createListOrderEvent.getCreateOrderEventList()
                .forEach(order -> order.getCreateOrderItemEventList().forEach(item -> {
                    if (FnCommon.isNotNull(item.getFlashSaleProductId())) {
                        FlashSaleProduct flashSaleProduct = flashSaleProductRepository
                                .findById(item.getFlashSaleProductId())
                                .orElseThrow(() -> new NotFoundException(MessageError.FLASH_SALE_PRODUCT_NOT_FOUND));
                        UserPurchaseLimit userPurchaseLimit = userPurchaseLimitRepository
                                .findByUserIdAndFlashSaleProduct(userId, flashSaleProduct)
                                .orElse(UserPurchaseLimit.builder()
                                        .userId(userId)
                                        .flashSaleProduct(flashSaleProduct)
                                        .purchasedQuantity(0)
                                        .build());
                        int quantityAllowPurchaseDiscount = Math.min(
                                flashSaleProduct.getMaxQuantityPerUser() - userPurchaseLimit.getPurchasedQuantity(),
                                item.getQuantity());
                        quantityAllowPurchaseDiscount = Math.min(quantityAllowPurchaseDiscount,
                                flashSaleProduct.getTotalQuantity() - flashSaleProduct.getSoldQuantity());
                        if (quantityAllowPurchaseDiscount > 0) {
                            flashSaleProduct.setSoldQuantity(
                                    flashSaleProduct.getSoldQuantity() + quantityAllowPurchaseDiscount);
                            if (Objects.equals(flashSaleProduct.getSoldQuantity(), flashSaleProduct.getTotalQuantity())) {
                                flashSaleProduct.setIsSoldOut(true);
                            }
                            userPurchaseLimit.setPurchasedQuantity(
                                    userPurchaseLimit.getPurchasedQuantity() + quantityAllowPurchaseDiscount);
                            flashSaleProductRepository.save(flashSaleProduct);
                            userPurchaseLimitRepository.save(userPurchaseLimit);
                            item.setDiscount(flashSaleProduct.getDiscountPercentage());
                            item.setQuantityDiscount(quantityAllowPurchaseDiscount);
                        }else {
                            order.setOrderStatus(OrderStatus.CANCELLED);
                            order.setReason("Sản phẩm không đủ số lượng");
                        }
                    }
                }));
        orderFlashSaleProducer.send(createListOrderEvent);
    }

    @Override
    @Transactional
    public void restoreFlashSaleStock(RestoreFlashSaleStockEvent event) {
        event.getRestoreFlashSaleItems().forEach(item -> {
            FlashSaleProduct flashSaleProduct = flashSaleProductRepository.findById(item.getFlashSaleProductId())
                    .orElse(null);
            if (flashSaleProduct != null) {
                flashSaleProduct.setSoldQuantity(Math.max(0, flashSaleProduct.getSoldQuantity() - item.getQuantity()));
                if (flashSaleProduct.getIsSoldOut()
                        && flashSaleProduct.getSoldQuantity() < flashSaleProduct.getTotalQuantity()) {
                    flashSaleProduct.setIsSoldOut(false);
                }
                flashSaleProductRepository.save(flashSaleProduct);

                UserPurchaseLimit userPurchaseLimit = userPurchaseLimitRepository
                        .findByUserIdAndFlashSaleProduct(event.getUserId(), flashSaleProduct).orElse(null);
                if (userPurchaseLimit != null) {
                    userPurchaseLimit.setPurchasedQuantity(
                            Math.max(0, userPurchaseLimit.getPurchasedQuantity() - item.getQuantity()));
                    userPurchaseLimitRepository.save(userPurchaseLimit);
                }
            }
        });
    }
}
