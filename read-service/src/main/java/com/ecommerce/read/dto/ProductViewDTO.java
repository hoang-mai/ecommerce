package com.ecommerce.read.dto;

import com.ecommerce.library.enumeration.ProductStatus;
import com.ecommerce.library.enumeration.ProductVariantStatus;
import com.ecommerce.library.enumeration.ShopStatus;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductViewDTO {
    @JsonProperty("productId")
    private String _id;
    private String shopId;
    private Double rating;
    private String name;
    private String description;
    private ProductStatus productStatus;
    private Integer totalSold;
    private Double discount;
    private LocalDateTime discountStartDate;
    private LocalDateTime discountEndDate;
    private String categoryId;
    private String categoryName;
    private ShopStatus shopStatus;
    private List<ProductImageDTO> productImages;
    private List<ProductAttributeDTO> productAttributes;
    private List<ProductVariantDTO> productVariants;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductImageDTO {
        @JsonProperty("productImageId")
        private String _id;
        private String url;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductAttributeDTO {
        @JsonProperty("productAttributeId")
        private String _id;
        private String productAttributeName;
        private List<ProductAttributeValueDTO> productAttributeValues;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductAttributeValueDTO {
        @JsonProperty("productAttributeValueId")
        private String _id;
        private String productAttributeValue;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductVariantDTO {
        @JsonProperty("productVariantId")
        private String _id;
        private ProductVariantStatus productVariantStatus;
        private BigDecimal price;
        private Integer stockQuantity;
        private Integer sold;
        private Boolean isDefault;
        private List<ProductVariantAttributeValueDTO> productVariantAttributeValues;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductVariantAttributeValueDTO {
        @JsonProperty("productVariantAttributeValueId")
        private String _id;
        private String productAttributeId;
        private String productAttributeValueId;
    }
}
