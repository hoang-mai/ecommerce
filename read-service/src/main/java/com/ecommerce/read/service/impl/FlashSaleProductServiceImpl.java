package com.ecommerce.read.service.impl;

import com.ecommerce.library.component.UserHelper;
import com.ecommerce.library.exception.NotFoundException;
import com.ecommerce.library.kafka.event.flash.sale.FlashSaleProductEvent;
import com.ecommerce.library.utils.MessageError;
import com.ecommerce.library.utils.PageResponse;
import com.ecommerce.read.dto.FlashSaleStatisticDTO;
import com.ecommerce.read.entity.FlashSaleProductView;
import com.ecommerce.read.entity.ProductView;
import com.ecommerce.read.repository.FlashSaleProductViewRepository;
import com.ecommerce.read.repository.ProductViewRepository;
import com.ecommerce.read.repository.impl.FlashSaleProductViewRepositoryImpl;
import com.ecommerce.read.service.FileService;
import com.ecommerce.read.service.FlashSaleProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FlashSaleProductServiceImpl implements FlashSaleProductService {
    private final FlashSaleProductViewRepository flashSaleProductViewRepository;
    private final FlashSaleProductViewRepositoryImpl flashSaleProductViewRepositoryImpl;
    private final ProductViewRepository productViewRepository;
    private final FileService fileService;
    private final UserHelper userHelper;

    @Override
    public void createFlashSaleProduct(FlashSaleProductEvent event) {
        ProductView productView = productViewRepository.findById(String.valueOf(event.getProductId()))
                .orElseThrow(() -> new NotFoundException(MessageError.PRODUCT_NOT_FOUND));

        ProductView.ProductVariant productVariant = productView.getProductVariants().stream()
                .filter(variant -> variant.get_id().equals(String.valueOf(event.getProductVariantId())))
                .findFirst()
                .orElse(null);

        List<FlashSaleProductView.ProductAttribute> flashSaleAttributes = new ArrayList<>();
        if (productVariant != null && productVariant.getProductVariantAttributeValues() != null) {
            for (ProductView.ProductVariantAttributeValue variantAttrValue : productVariant
                    .getProductVariantAttributeValues()) {
                productView.getProductAttributes().stream()
                        .filter(attr -> attr.get_id().equals(variantAttrValue.getProductAttributeId()))
                        .findFirst()
                        .ifPresent(attribute -> {
                            attribute.getProductAttributeValues().stream()
                                    .filter(attrVal -> attrVal.get_id()
                                            .equals(variantAttrValue.getProductAttributeValueId()))
                                    .findFirst()
                                    .ifPresent(attributeValue -> {
                                        flashSaleAttributes.add(FlashSaleProductView.ProductAttribute.builder()
                                                .attributeName(attribute.getProductAttributeName())
                                                .attributeValue(attributeValue.getProductAttributeValue())
                                                .build());
                                    });
                        });
            }
        }

        FlashSaleProductView flashSaleProductView = FlashSaleProductView.builder()
                .flashSaleProductId(String.valueOf(event.getFlashSaleProductId()))
                .ownerId(String.valueOf(event.getOwnerId()))
                .shopId(String.valueOf(event.getShopId()))
                .productId(String.valueOf(event.getProductId()))
                .productVariantId(String.valueOf(event.getProductVariantId()))
                .originalPrice(event.getOriginalPrice())
                .discountPercentage(event.getDiscountPercentage())
                .totalQuantity(event.getTotalQuantity())
                .soldQuantity(event.getSoldQuantity())
                .maxQuantityPerUser(event.getMaxQuantityPerUser())
                .isSoldOut(event.getIsSoldOut())
                .score(event.getScore())
                .flashSaleCampaignId(String.valueOf(event.getFlashSaleCampaignId()))
                .flashSaleCampaignName(event.getFlashSaleCampaignName())
                .startTime(event.getStartTime())
                .endTime(event.getEndTime())
                .productName(productView.getName())
                .productImages(productView.getProductImages())
                .productAttributes(flashSaleAttributes)
                .createdAt(event.getCreatedAt())
                .totalSold(event.getTotalSold())
                .rating(productView.getRating())
                .build();
        flashSaleProductView.setSalePrice(flashSaleProductView.calculateSalePrice());
        flashSaleProductView.setTotalRevenue(flashSaleProductView.calculateTotalRevenue());
        flashSaleProductViewRepository.save(flashSaleProductView);
    }

    @Override
    public void updateFlashSaleProduct(FlashSaleProductEvent event) {
        FlashSaleProductView flashSaleProductView = flashSaleProductViewRepository
                .findById(String.valueOf(event.getFlashSaleProductId()))
                .orElseThrow(() -> new NotFoundException(MessageError.FLASH_SALE_PRODUCT_NOT_FOUND));

        flashSaleProductView.setDiscountPercentage(event.getDiscountPercentage());
        flashSaleProductView.setTotalQuantity(event.getTotalQuantity());
        flashSaleProductView.setSoldQuantity(event.getSoldQuantity());
        flashSaleProductView.setMaxQuantityPerUser(event.getMaxQuantityPerUser());
        flashSaleProductView.setIsSoldOut(event.getIsSoldOut());
        flashSaleProductView.setScore(event.getScore());
        flashSaleProductView.setStartTime(event.getStartTime());
        flashSaleProductView.setEndTime(event.getEndTime());
        flashSaleProductView.setSalePrice(flashSaleProductView.calculateSalePrice());
        flashSaleProductView.setTotalRevenue(flashSaleProductView.calculateTotalRevenue());

        flashSaleProductViewRepository.save(flashSaleProductView);
    }

    @Override
    public void deleteFlashSaleProduct(Long flashSaleProductId) {
        flashSaleProductViewRepository.deleteById(String.valueOf(flashSaleProductId));
    }

    @Override
    public PageResponse<FlashSaleProductView> getFlashSaleProducts(
            String flashSaleCampaignId,
            String shopId,
            Boolean isOwner,
            int pageNo,
            int pageSize,
            String sortBy,
            String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
        Long ownerId = null;
        if(Boolean.TRUE.equals(isOwner)) {
            ownerId = userHelper.getCurrentUserId();
        }

        Page<FlashSaleProductView> page = flashSaleProductViewRepositoryImpl.getFlashSaleProducts(
                flashSaleCampaignId,
                shopId,
                ownerId!=null ? String.valueOf(ownerId) : null,
                pageable);

        return PageResponse.<FlashSaleProductView>builder()
                .data(page.getContent().stream()
                        .peek(flashSaleProductView -> flashSaleProductView.getProductImages()
                                .forEach(image -> image.setImageUrl(fileService.getPresignedUrl(image.getImageUrl()))))
                        .toList())
                .pageNo(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .hasNextPage(page.hasNext())
                .hasPreviousPage(page.hasPrevious())
                .build();
    }

    @Override
    public PageResponse<FlashSaleProductView> getCurrentFlashSaleProducts(
            int pageNo,
            int pageSize,
            String sortBy,
            String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        Page<FlashSaleProductView> page = flashSaleProductViewRepositoryImpl.getCurrentFlashSaleProducts(pageable);

        return PageResponse.<FlashSaleProductView>builder()
                .data(page.getContent().stream()
                        .peek(flashSaleProductView -> flashSaleProductView.getProductImages()
                                .forEach(image -> image.setImageUrl(fileService.getPresignedUrl(image.getImageUrl()))))
                        .toList())
                .pageNo(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .hasNextPage(page.hasNext())
                .hasPreviousPage(page.hasPrevious())
                .build();
    }

    @Override
    public FlashSaleStatisticDTO getFlashSaleProductStatistics(String flashSaleCampaignId, Boolean isOwner) {
        Long ownerId = null;
        if (Boolean.TRUE.equals(isOwner)) {
            ownerId = userHelper.getCurrentUserId();
        }
        return flashSaleProductViewRepositoryImpl.getFlashSaleProductStatistics(flashSaleCampaignId, ownerId);
    }

    @Override
    public void updateFlashSaleProductSold(String flashSaleProductId, Integer quantity,
            java.math.BigDecimal totalFinalPrice) {
        FlashSaleProductView flashSaleProductView = flashSaleProductViewRepository
                .findById(flashSaleProductId)
                .orElseThrow(() -> new NotFoundException(MessageError.FLASH_SALE_PRODUCT_NOT_FOUND));

        if (flashSaleProductView.getSoldQuantity() == null) {
            flashSaleProductView.setSoldQuantity(0);
        }
        if (flashSaleProductView.getTotalRevenue() == null) {
            flashSaleProductView.setTotalRevenue(java.math.BigDecimal.ZERO);
        }

        flashSaleProductView.setSoldQuantity(flashSaleProductView.getSoldQuantity() + quantity);
        flashSaleProductView.setTotalRevenue(flashSaleProductView.getTotalRevenue().add(totalFinalPrice));

        if (flashSaleProductView.getSoldQuantity() >= flashSaleProductView.getTotalQuantity()) {
            flashSaleProductView.setIsSoldOut(true);
        }

        flashSaleProductViewRepository.save(flashSaleProductView);
    }

    @Override
    public void restoreFlashSaleProductStock(
            com.ecommerce.library.kafka.event.flashsale.RestoreFlashSaleStockEvent event) {
        event.getRestoreFlashSaleItems().forEach(item -> {
            FlashSaleProductView flashSaleProductView = flashSaleProductViewRepository
                    .findById(String.valueOf(item.getFlashSaleProductId()))
                    .orElse(null);

            if (flashSaleProductView != null) {
                if (flashSaleProductView.getSoldQuantity() != null) {
                    flashSaleProductView
                            .setSoldQuantity(Math.max(0, flashSaleProductView.getSoldQuantity() - item.getQuantity()));
                }
                if (flashSaleProductView.getTotalRevenue() != null && item.getTotalFinalPrice() != null) {
                    java.math.BigDecimal newRevenue = flashSaleProductView.getTotalRevenue()
                            .subtract(item.getTotalFinalPrice());
                    flashSaleProductView.setTotalRevenue(
                            newRevenue.compareTo(java.math.BigDecimal.ZERO) < 0 ? java.math.BigDecimal.ZERO
                                    : newRevenue);
                }
                if (flashSaleProductView.getIsSoldOut() != null && flashSaleProductView.getIsSoldOut()
                        && flashSaleProductView.getSoldQuantity() < flashSaleProductView.getTotalQuantity()) {
                    flashSaleProductView.setIsSoldOut(false);
                }
                flashSaleProductViewRepository.save(flashSaleProductView);
            }
        });
    }
}
