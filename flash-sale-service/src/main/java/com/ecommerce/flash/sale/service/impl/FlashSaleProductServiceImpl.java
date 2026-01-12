package com.ecommerce.flash.sale.service.impl;

import com.ecommerce.flash.sale.dto.ReqCreateFlashSaleProductDTO;
import com.ecommerce.flash.sale.dto.ReqUpdateFlashSaleProductDTO;
import com.ecommerce.flash.sale.entity.FlashSaleCampaign;
import com.ecommerce.flash.sale.entity.FlashSaleProduct;
import com.ecommerce.flash.sale.messaging.producer.FlashSaleProductProducer;
import com.ecommerce.flash.sale.repository.FlashSaleCampaignRepository;
import com.ecommerce.flash.sale.repository.FlashSaleProductRepository;
import com.ecommerce.flash.sale.service.FlashSaleProductService;
import com.ecommerce.library.component.UserHelper;
import com.ecommerce.library.exception.NotFoundException;
import com.ecommerce.library.kafka.event.flash.sale.FlashSaleProductEvent;
import com.ecommerce.library.utils.MessageError;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

import static com.ecommerce.library.utils.Constant.MAX_DISCOUNT_PERCENTAGE;
import static com.ecommerce.library.utils.Constant.MIN_DISCOUNT_PERCENTAGE;

@Service
@RequiredArgsConstructor
public class FlashSaleProductServiceImpl implements FlashSaleProductService {

    private final FlashSaleProductRepository flashSaleProductRepository;
    private final FlashSaleCampaignRepository flashSaleCampaignRepository;
    private final UserHelper userHelper;
    private final FlashSaleProductProducer flashSaleProductProducer;


    @Transactional
    @Override
    public void createFlashSaleProduct(ReqCreateFlashSaleProductDTO request) {
        Long userId = userHelper.getCurrentUserId();
        FlashSaleCampaign campaign = flashSaleCampaignRepository.findByFlashSaleCampaignId(request.getCampaignId())
            .orElseThrow(() -> new NotFoundException(MessageError.FLASH_SALE_CAMPAIGN_NOT_FOUND));

        if (flashSaleProductRepository.existsByFlashSaleCampaign_FlashSaleCampaignIdAndProductIdAndProductVariantId(
            request.getCampaignId(), request.getProductId(), request.getProductVariantId())) {
            throw new IllegalArgumentException(MessageError.FLASH_SALE_PRODUCT_ALREADY_EXISTS_IN_CAMPAIGN);
        }

        validateDiscountPercentage(request.getDiscountPercentage());
        if (request.getMaxQuantityPerUser() > request.getTotalQuantity()) {
            throw new IllegalArgumentException(MessageError.MAX_QUANTITY_PER_USER_EXCEEDS_TOTAL_QUANTITY);
        }

        double score = calculateProductScore(request.getDiscountPercentage(), request.getRating(), request.getTotalSold(), request.getTotalQuantity());

        FlashSaleProduct flashSaleProduct = FlashSaleProduct.builder()
            .ownerId(userId)
            .shopId(request.getShopId())
            .productId(request.getProductId())
            .productVariantId(request.getProductVariantId())
            .discountPercentage(request.getDiscountPercentage())
            .totalQuantity(request.getTotalQuantity())
            .soldQuantity(0)
            .maxQuantityPerUser(request.getMaxQuantityPerUser())
            .isSoldOut(false)
            .score(score)
            .flashSaleCampaign(campaign)
            .build();

        flashSaleProductRepository.save(flashSaleProduct);
        campaign.setCountRegisteredProducts(campaign.getCountRegisteredProducts() != null ? campaign.getCountRegisteredProducts() + 1 : 1);
        flashSaleCampaignRepository.save(campaign);
        flashSaleProductProducer.send(
            FlashSaleProductEvent.builder()
                .flashSaleCampaignId(campaign.getFlashSaleCampaignId())
                .flashSaleProductId(flashSaleProduct.getFlashSaleProductId())
                .ownerId(flashSaleProduct.getOwnerId())
                .shopId(flashSaleProduct.getShopId())
                .productId(flashSaleProduct.getProductId())
                .productVariantId(flashSaleProduct.getProductVariantId())
                .originalPrice(request.getOriginalPrice())
                .discountPercentage(flashSaleProduct.getDiscountPercentage())
                .totalQuantity(flashSaleProduct.getTotalQuantity())
                .soldQuantity(flashSaleProduct.getSoldQuantity())
                .maxQuantityPerUser(flashSaleProduct.getMaxQuantityPerUser())
                .isSoldOut(flashSaleProduct.getIsSoldOut())
                .score(flashSaleProduct.getScore())
                .flashSaleCampaignName(campaign.getCampaignName())
                .startTime(campaign.getStartTime())
                .endTime(campaign.getEndTime())
                .totalSold(request.getTotalSold())
                .rating(request.getRating())
                .build()
        );
    }

    @Override
    public void updateFlashSaleProduct(Long flashSaleProductId, ReqUpdateFlashSaleProductDTO request) {
        FlashSaleProduct flashSaleProduct = flashSaleProductRepository.findByFlashSaleProductId(flashSaleProductId)
            .orElseThrow(() -> new NotFoundException(MessageError.FLASH_SALE_PRODUCT_NOT_FOUND));

        FlashSaleCampaign campaign = flashSaleProduct.getFlashSaleCampaign();
        Instant now = Instant.now();

        if (campaign.getStartTime().isBefore(now)) {
            throw new IllegalArgumentException(MessageError.CANNOT_UPDATE_FLASH_SALE_PRODUCT_ALREADY_STARTED);
        }

        validateDiscountPercentage(request.getDiscountPercentage());

        if (request.getMaxQuantityPerUser() > request.getTotalQuantity()) {
            throw new IllegalArgumentException(MessageError.MAX_QUANTITY_PER_USER_EXCEEDS_TOTAL_QUANTITY);
        }

        double score = calculateProductScore(
            request.getDiscountPercentage(),
            request.getRating(),
            request.getTotalSold(),
            request.getTotalQuantity()
        );

        flashSaleProduct.setDiscountPercentage(request.getDiscountPercentage());
        flashSaleProduct.setTotalQuantity(request.getTotalQuantity());
        flashSaleProduct.setMaxQuantityPerUser(request.getMaxQuantityPerUser());
        flashSaleProduct.setScore(score);

        if (flashSaleProduct.getSoldQuantity() >= request.getTotalQuantity()) {
            flashSaleProduct.setIsSoldOut(true);
        }

        flashSaleProductRepository.save(flashSaleProduct);

        flashSaleProductProducer.sendUpdate(
            FlashSaleProductEvent.builder()
                .flashSaleCampaignId(campaign.getFlashSaleCampaignId())
                .flashSaleProductId(flashSaleProduct.getFlashSaleProductId())
                .ownerId(flashSaleProduct.getOwnerId())
                .shopId(flashSaleProduct.getShopId())
                .productId(flashSaleProduct.getProductId())
                .productVariantId(flashSaleProduct.getProductVariantId())
                .originalPrice(request.getOriginalPrice())
                .discountPercentage(flashSaleProduct.getDiscountPercentage())
                .totalQuantity(flashSaleProduct.getTotalQuantity())
                .soldQuantity(flashSaleProduct.getSoldQuantity())
                .maxQuantityPerUser(flashSaleProduct.getMaxQuantityPerUser())
                .isSoldOut(flashSaleProduct.getIsSoldOut())
                .score(flashSaleProduct.getScore())
                .flashSaleCampaignName(campaign.getCampaignName())
                .startTime(campaign.getStartTime())
                .endTime(campaign.getEndTime())
                .build()
        );
    }
    @Transactional
    @Override
    public void deleteFlashSaleProduct(Long flashSaleProductId) {
        FlashSaleProduct flashSaleProduct = flashSaleProductRepository.findByFlashSaleProductId(flashSaleProductId)
            .orElseThrow(() -> new NotFoundException(MessageError.FLASH_SALE_PRODUCT_NOT_FOUND));

        FlashSaleCampaign campaign = flashSaleProduct.getFlashSaleCampaign();
        Instant now = Instant.now();

        if (campaign.getStartTime().isBefore(now)) {
            throw new IllegalArgumentException(MessageError.CANNOT_DELETE_FLASH_SALE_PRODUCT_ALREADY_STARTED);
        }
        campaign.setCountRegisteredProducts(campaign.getCountRegisteredProducts() - 1);
        flashSaleCampaignRepository.save(campaign);
        flashSaleProductRepository.delete(flashSaleProduct);

        flashSaleProductProducer.sendDelete(flashSaleProductId);
    }

    /**
     * Validate phần trăm giảm giá phải từ 20% đến 70%
     */
    private void validateDiscountPercentage(double discountPercentage) {
        if (discountPercentage < MIN_DISCOUNT_PERCENTAGE || discountPercentage > MAX_DISCOUNT_PERCENTAGE) {
            throw new IllegalArgumentException(MessageError.INVALID_DISCOUNT_PERCENTAGE);
        }
    }

    /**
     * Tính điểm cho sản phẩm dựa trên:
     * - Phần trăm giảm giá (40% trọng số)
     * - Rating (30% trọng số)
     * - Số lượng đã bán (20% trọng số)
     * - Tổng số lượng muốn bán (10% trọng số)
     */
    private double calculateProductScore(double discountPercentage, double rating, int totalSold, int totalQuantity) {
        double normalizedDiscount = (discountPercentage - MIN_DISCOUNT_PERCENTAGE) /
            (MAX_DISCOUNT_PERCENTAGE - MIN_DISCOUNT_PERCENTAGE);

        double normalizedRating = rating / 5.0;

        double normalizedSold = Math.log(1 + totalSold) / Math.log(10001);

        double normalizedTotalQuantity = Math.log(1 + totalQuantity) / Math.log(10001);

        double score = (normalizedDiscount * 40.0) +
            (normalizedRating * 30.0) +
            (normalizedSold * 20.0) +
            (normalizedTotalQuantity * 10.0);

        return Math.round(score * 100.0) / 100.0;
    }

}
