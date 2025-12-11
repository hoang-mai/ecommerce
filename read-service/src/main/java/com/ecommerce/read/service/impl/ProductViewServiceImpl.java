package com.ecommerce.read.service.impl;

import com.ecommerce.library.component.UserHelper;
import com.ecommerce.library.enumeration.*;
import com.ecommerce.library.exception.NotFoundException;
import com.ecommerce.library.kafka.event.order.CreateListOrderEvent;
import com.ecommerce.library.kafka.event.product.CreateProductEvent;
import com.ecommerce.library.kafka.event.product.UpdateProductStatusEvent;
import com.ecommerce.library.kafka.event.product.UpdateProductVariantStatusEvent;
import com.ecommerce.library.kafka.event.shop.UpdateShopStatusEvent;
import com.ecommerce.library.utils.MessageError;
import com.ecommerce.library.utils.PageResponse;
import com.ecommerce.read.dto.ProductViewStatisticDTO;
import com.ecommerce.read.entity.ProductView;
import com.ecommerce.read.repository.ProductViewRepository;
import com.ecommerce.read.repository.impl.ProductViewRepositoryImpl;
import com.ecommerce.read.repository.impl.ShopViewRepositoryImpl;
import com.ecommerce.read.service.FileService;
import com.ecommerce.read.service.ProductViewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Service
@RequiredArgsConstructor
public class ProductViewServiceImpl implements ProductViewService {
    private final ShopViewRepositoryImpl shopViewRepositoryImpl;
    private final ProductViewRepository productViewRepository;
    private final ProductViewRepositoryImpl productViewRepositoryImpl;
    private final FileService fileService;
    private final UserHelper userHelper;

    @Override
    public void createProductEvent(CreateProductEvent event) {
        ProductView productView = ProductView.builder()
                ._id(String.valueOf(event.getProductId()))
                .shopId(String.valueOf(event.getShopId()))
                .name(event.getProductName())
                .description(event.getDescription())
                .productStatus(event.getProductStatus())
                .discount(event.getDiscount())
                .discountStartDate(event.getDiscountStartDate())
                .discountEndDate(event.getDiscountEndDate())
                .ownerId(String.valueOf(event.getOwnerId()))
                .createdAt(event.getCreatedAt())
                .updatedAt(event.getUpdatedAt())
                .categoryId(event.getCategoryId() == null ? null : String.valueOf(event.getCategoryId()))
                .categoryName(event.getCategoryName())
                .shopId(event.getShopId() == null ? null : String.valueOf(event.getShopId()))
                .shopStatus(event.getShopStatus())
                .productImages(event.getProductImages() == null ? null : event.getProductImages().stream()
                        .map(img -> ProductView.ProductImage.builder()
                                ._id(String.valueOf(img.getProductImageId()))
                                .imageUrl(img.getImageUrl())
                                .build())
                        .toList())
                .productAttributes(event.getProductAttributes() == null ? null : event.getProductAttributes().stream()
                        .map(attr -> ProductView.ProductAttribute.builder()
                                ._id(String.valueOf(attr.getProductAttributeId()))
                                .productAttributeName(attr.getProductAttributeName())
                                .productAttributeValues(attr.getProductAttributeValues() == null ? null : attr.getProductAttributeValues().stream()
                                        .map(val -> ProductView.ProductAttributeValue.builder()
                                                ._id(String.valueOf(val.getProductAttributeValueId()))
                                                .productAttributeValue(val.getValue())
                                                .build())
                                        .toList())
                                .build())
                        .toList())
                .productVariants(event.getProductVariants() == null ? null : event.getProductVariants().stream()
                        .map(variant -> ProductView.ProductVariant.builder()
                                ._id(String.valueOf(variant.getProductVariantId()))
                                .price(variant.getPrice())
                                .stockQuantity(variant.getStockQuantity())
                                .productVariantStatus(variant.getProductVariantStatus())
                                .isDefault(variant.getIsDefault())
                                .productVariantAttributeValues(variant.getProductVariantAttributeValues() == null ? null : variant.getProductVariantAttributeValues().stream()
                                        .map(val -> ProductView.ProductVariantAttributeValue.builder()
                                                ._id(String.valueOf(val.getProductVariantAttributeValueId()))
                                                .productAttributeId(String.valueOf(val.getProductAttributeId()))
                                                .productAttributeValueId(String.valueOf(val.getProductAttributeValueId()))
                                                .build())
                                        .toList())
                                .build())
                        .toList())
                .build();
        productViewRepository.save(productView);
        if(Boolean.TRUE.equals(event.getCreated())){
            shopViewRepositoryImpl.incrementProductCount(event.getShopId());
        }
    }

    @Override
    public void updateProductStatus(UpdateProductStatusEvent event) {
        ProductView productView = productViewRepository.findById(String.valueOf(event.getProductId()))
                .orElseThrow(() -> new NotFoundException(MessageError.PRODUCT_NOT_FOUND));
        productView.setProductStatus(event.getStatus());
        productViewRepository.save(productView);
        shopViewRepositoryImpl.updateProductStatusInShopView(productView.getShopId(), event.getStatus());
    }

    @Override
    public void updateProductVariantStatus(UpdateProductVariantStatusEvent event) {
        productViewRepositoryImpl.updateProductVariantStatus(event);
    }

    @Override
    public PageResponse<ProductView> searchProducts(Boolean isOwner, Long shopId, Long categoryId, ProductStatus status, String keyword, Integer star, Double startPrice, Double endPrice, int pageNo, int pageSize, String sortBy, String sortDir) {
        Long ownerId = null;
        ShopStatus shopStatus = null;
        if (Boolean.TRUE.equals(isOwner)) {
            ownerId = userHelper.getCurrentUserId();
        } else {
            status = ProductStatus.ACTIVE;
            shopStatus = ShopStatus.ACTIVE;
        }
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        Page<ProductView> productsPage = productViewRepositoryImpl.getProductView(ownerId, shopId, categoryId, status, shopStatus, keyword, star, startPrice, endPrice, pageable);

        return PageResponse.<ProductView>builder()
                .data(productsPage.getContent().stream().peek(productView ->
                        productView.getProductImages().forEach(productImage ->
                                productImage.setImageUrl(fileService.getPresignedUrl(productImage.getImageUrl()))
                        )).toList())
                .pageNo(productsPage.getNumber())
                .pageSize(productsPage.getSize())
                .totalElements(productsPage.getTotalElements())
                .totalPages(productsPage.getTotalPages())
                .build();
    }

    @Override
    public ProductView getProductById(Long productId, boolean isOwner) {
        if (isOwner) {
            Long ownerId = userHelper.getCurrentUserId();
            ProductView productView = productViewRepository.findBy_idAndOwnerId(String.valueOf(productId), String.valueOf(ownerId))
                    .orElseThrow(() -> new NotFoundException(MessageError.PRODUCT_NOT_FOUND));
            productView.getProductImages().forEach(productImage ->
                    productImage.setImageUrl(fileService.getPresignedUrl(productImage.getImageUrl()))
            );
            return productView;
        }
        ProductView productView = productViewRepository.findBy_idAndProductStatusAndShopStatus(String.valueOf(productId), ProductStatus.ACTIVE, ShopStatus.ACTIVE)
                .orElseThrow(() -> new NotFoundException(MessageError.PRODUCT_NOT_FOUND));
        productView.getProductImages().forEach(productImage ->
                productImage.setImageUrl(fileService.getPresignedUrl(productImage.getImageUrl()))
        );
        return productView;
    }

    @Override
    public void updateStockAfterCreateOrder(CreateListOrderEvent createListOrderEvent) {

        createListOrderEvent.getCreateOrderEventList().forEach(createOrderEvent -> {
            AtomicLong totalSoldForShop= new AtomicLong();
            if (OrderStatus.CANCELLED.equals(createOrderEvent.getOrderStatus())) {
                return;
            }
            createOrderEvent.getCreateOrderItemEventList().forEach(createOrderItemEvent -> {
                ProductView productView = productViewRepository.findById(String.valueOf(createOrderItemEvent.getProductId()))
                        .orElseThrow(() -> new NotFoundException(MessageError.PRODUCT_NOT_FOUND));

                String variantId = String.valueOf(createOrderItemEvent.getProductVariantId());

                ProductView.ProductVariant matchedVariant = productView.getProductVariants().stream()
                        .filter(v -> variantId.equals(v.get_id()))
                        .findFirst()
                        .orElseThrow(() -> new NotFoundException(MessageError.PRODUCT_VARIANT_NOT_FOUND));

                int currentStock = matchedVariant.getStockQuantity() == null ? 0 : matchedVariant.getStockQuantity();
                int quantity = createOrderItemEvent.getQuantity() == null ? 0 : createOrderItemEvent.getQuantity();
                int updatedStock = currentStock - quantity;

                if (updatedStock <= 0) {
                    matchedVariant.setStockQuantity(0);
                    matchedVariant.setProductVariantStatus(ProductVariantStatus.OUT_OF_STOCK);
                } else {
                    matchedVariant.setStockQuantity(updatedStock);
                }

                matchedVariant.addSold(quantity);
                productView.addSold(quantity);
                productViewRepository.save(productView);
                totalSoldForShop.addAndGet(quantity);
            });
            shopViewRepositoryImpl.incrementTotalSoldAndTotalOrder(String.valueOf(createOrderEvent.getShopId()), totalSoldForShop.intValue());
        });

    }

    @Override
    public void updateShopStatusInProductViews(UpdateShopStatusEvent updateShopStatusEvent) {
        List<ProductView> productViews = productViewRepository.findByShopId(String.valueOf(updateShopStatusEvent.getShopId()));
        productViews.forEach(productView -> productView.setShopStatus(updateShopStatusEvent.getShopStatus()));
        productViewRepository.saveAll(productViews);
    }


    @Override
    public void updateRating(Long productId, RatingNumber rating, Boolean isUpdate, RatingNumber oldRating, Boolean isDelete) {
        productViewRepositoryImpl.updateRating(productId, rating, isUpdate, oldRating, isDelete);
        ProductView productView = productViewRepository.findById(String.valueOf(productId))
                .orElseThrow(() -> new NotFoundException(MessageError.PRODUCT_NOT_FOUND));
        shopViewRepositoryImpl.updateRating(productView.getShopId(), rating, isUpdate, oldRating, isDelete);
    }

    @Override
    public List<ProductViewStatisticDTO> getProductStatistics(String shopId, Boolean isOwner, LocalDateTime nowDate) {
        Long currentUserId = null;
        if (Boolean.TRUE.equals(isOwner)) {
            currentUserId = userHelper.getCurrentUserId();
        }
        return productViewRepositoryImpl.getProductStatistics(shopId, isOwner, currentUserId, nowDate);
    }

}

