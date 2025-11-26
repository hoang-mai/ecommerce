package com.ecommerce.library.kafka.event.product;

import com.ecommerce.library.enumeration.ProductStatus;
import com.ecommerce.library.enumeration.ProductVariantStatus;
import com.ecommerce.library.enumeration.ShopStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CreateProductEvent {
    private Long productId;
    private Long shopId;
    private Long categoryId;
    private String categoryName;
    private ShopStatus shopStatus;
    private Long ownerId;
    private String productName;
    private String description;
    private ProductStatus productStatus;
    private Integer totalSold;
    private Double discount;
    private LocalDateTime discountStartDate;
    private LocalDateTime discountEndDate;
    private List<CreateProductImageEvent> productImages;
    private List<CreateProductAttributeEvent> productAttributes;
    private List<CreateProductVariantEvent> productVariants;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CreateProductAttributeEvent {

        private Long productAttributeId;
        private String productAttributeName;
        private List<CreateProductAttributeValueEvent> productAttributeValues;
    }

    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CreateProductAttributeValueEvent {
        private Long productAttributeValueId;
        private String value;
    }

    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CreateProductImageEvent {
        private Long productImageId;
        private String imageUrl;
    }

    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CreateProductVariantEvent {
        private Long productVariantId;
        private BigDecimal price;
        private Integer stockQuantity;
        private Integer soldQuantity;
        private ProductVariantStatus productVariantStatus;
        private Boolean isDefault;
        private List<CreateProductVariantValueEvent> productVariantAttributeValues;
    }

    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CreateProductVariantValueEvent {

        private Long productVariantAttributeValueId;
        private Long productAttributeId;
        private Long productAttributeValueId;

    }

    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CreateProductShopEvent {
        private Long shopId;
        private String shopName;
    }
}
