package com.ecommerce.read.entity;

import com.ecommerce.library.enumeration.ProductStatus;
import com.ecommerce.library.enumeration.ProductVariantStatus;
import com.ecommerce.library.enumeration.ShopStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "product_views")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ProductView extends BaseEntity {
    @Id
    @Field("_id")
    @JsonProperty("productId")
    private String _id;

    @Field("rating")
    @Builder.Default
    private Double rating = 0.0;

    @Field("name")
    private String name;

    @Field("description")
    private String description;

    @Field("productStatus")
    private ProductStatus productStatus;

    @Field("totalSold")
    @Builder.Default
    private Integer totalSold = 0;

    @Field("discount")
    @Builder.Default
    private Double discount = 0.0;

    @Field("discountStartDate")
    private LocalDateTime discountStartDate;

    @Field("discountEndDate")
    private LocalDateTime discountEndDate;

    @Field("categoryId")
    private String categoryId;

    @Field("categoryName")
    private String categoryName;

    @Field("shopId")
    private String shopId;

    @Field(name = "shopStatus")
    private ShopStatus shopStatus;

    @Field("ownerId")
    private String ownerId;

    @Field("productImages")
    @Builder.Default
    private List<ProductImage> productImages = new ArrayList<>();

    @Field("productAttributes")
    @Builder.Default
    private List<ProductAttribute> productAttributes = new ArrayList<>();

    @Field("productVariants")
    @Builder.Default
    private List<ProductVariant> productVariants = new ArrayList<>();

    public void addSold(Integer quantity) {
        totalSold += quantity;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductImage {
        @Id
        @Field("_id")
        @JsonProperty("productImageId")
        private String _id;
        @Field("imageUrl")
        private String imageUrl;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductAttribute {
        @Id
        @Field("_id")
        @JsonProperty("productAttributeId")
        private String _id;

        @Field("productAttributeName")
        private String productAttributeName;
        @Builder.Default
        @Field("productAttributeValues")
        private List<ProductAttributeValue> productAttributeValues = new ArrayList<>();
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductAttributeValue {
        @Id
        @Field("_id")
        @JsonProperty("productAttributeValueId")
        private String _id;
        @Field("productAttributeValue")
        private String productAttributeValue;
    }


    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductVariant {
        @Id
        @Field("_id")
        @JsonProperty("productVariantId")
        private String _id;

        @Field("productVariantStatus")
        private ProductVariantStatus productVariantStatus;

        @Field("price")
        private BigDecimal price;

        @Field("stockQuantity")
        private Integer stockQuantity;

        @Field("sold")
        @Builder.Default
        private Integer sold = 0;

        @Field("isDefault")
        @Builder.Default
        private Boolean isDefault = false;

        @Builder.Default
        @Field("productVariantAttributeValues")
        private List<ProductVariantAttributeValue> productVariantAttributeValues = new ArrayList<>();

        public void addSold(Integer quantity) {
            this.sold += quantity;
        }
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductVariantAttributeValue {
        @Id
        @Field("_id")
        @JsonProperty("productVariantAttributeValueId")
        private String _id;
        @Field("productAttributeId")
        private String productAttributeId;
        @Field("productAttributeValueId")
        private String productAttributeValueId;
    }

}
