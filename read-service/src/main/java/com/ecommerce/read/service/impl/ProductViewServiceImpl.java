package com.ecommerce.read.service.impl;

import com.ecommerce.library.exception.NotFoundException;
import com.ecommerce.library.kafka.event.product.CreateProductEvent;
import com.ecommerce.library.kafka.event.product.UpdateProductStatusEvent;
import com.ecommerce.library.kafka.event.product.UpdateProductVariantStatusEvent;
import com.ecommerce.library.utils.MessageError;
import com.ecommerce.read.entity.ProductView;
import com.ecommerce.read.repository.ProductViewRepository;
import com.ecommerce.read.repository.impl.ProductViewRepositoryImpl;
import com.ecommerce.read.service.ProductViewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductViewServiceImpl implements ProductViewService {
    private final ProductViewRepository productViewRepository;
    private final ProductViewRepositoryImpl productViewRepositoryImpl;

    @Override
    public void createProductEvent(CreateProductEvent event) {
        ProductView productView = ProductView.builder()
                .productId(String.valueOf(event.getProductId()))
                .shopId(String.valueOf(event.getShopId()))
                .name(event.getProductName())
                .description(event.getDescription())
                .productStatus(event.getProductStatus())
                .totalSold(event.getTotalSold())
                .discount(event.getDiscount())
                .discountStartDate(event.getDiscountStartDate())
                .discountEndDate(event.getDiscountEndDate())
                .categoryId(event.getCategoryId() == null ? null : String.valueOf(event.getCategoryId()))
                .productImages(event.getProductImages() == null ? null : event.getProductImages().stream()
                        .map(img -> ProductView.ProductImage.builder()
                                .productImageId(String.valueOf(img.getProductImageId()))
                                .url(img.getImageUrl())
                                .build())
                        .toList())
                .productAttributes(event.getProductAttributes() == null ? null : event.getProductAttributes().stream()
                        .map(attr -> ProductView.ProductAttribute.builder()
                                .productAttributeId(String.valueOf(attr.getProductAttributeId()))
                                .productAttributeName(attr.getProductAttributeName())
                                .productAttributeValues(attr.getProductAttributeValues() == null ? null : attr.getProductAttributeValues().stream()
                                        .map(val -> ProductView.ProductAttributeValue.builder()
                                                .productAttributeValueId(String.valueOf(val.getProductAttributeValueId()))
                                                .productAttributeValue(val.getValue())
                                                .build())
                                        .toList())
                                .build())
                        .toList())
                .productVariants(event.getProductVariants() == null ? null : event.getProductVariants().stream()
                        .map(variant -> ProductView.ProductVariant.builder()
                                .productVariantId(String.valueOf(variant.getProductVariantId()))
                                .price(variant.getPrice())
                                .stockQuantity(variant.getStockQuantity())
                                .sold(variant.getSoldQuantity())
                                .productVariantStatus(variant.getProductVariantStatus())
                                .isDefault(variant.getIsDefault())
                                .productVariantAttributeValues(variant.getProductVariantAttributeValues() == null ? null : variant.getProductVariantAttributeValues().stream()
                                        .map(val -> ProductView.ProductVariantAttributeValue.builder()
                                                .productVariantAttributeValueId(String.valueOf(val.getProductVariantAttributeValueId()))
                                                .productAttributeId(String.valueOf(val.getProductAttributeId()))
                                                .productAttributeValueId(String.valueOf(val.getProductAttributeValueId()))
                                                .build())
                                        .toList())
                                .build())
                        .toList())
                .build();
        productViewRepository.save(productView);
    }

    @Override
    public void updateProductStatus(UpdateProductStatusEvent event) {
        ProductView productView = productViewRepository.findById(String.valueOf(event.getProductId()))
                .orElseThrow(() -> new NotFoundException(MessageError.PRODUCT_NOT_FOUND));
        productView.setProductStatus(event.getStatus());
        productViewRepository.save(productView);
    }

    @Override
    public void updateProductVariantStatus(UpdateProductVariantStatusEvent event) {
        productViewRepositoryImpl.updateProductVariantStatus(event);
    }
}
