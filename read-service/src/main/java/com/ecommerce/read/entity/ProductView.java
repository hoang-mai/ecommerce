package com.ecommerce.read.entity;

import com.ecommerce.library.enumeration.ProductStatus;
import com.ecommerce.library.enumeration.ProductVariantStatus;
import lombok.*;
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
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductView extends BaseEntity {
    @Id
    @Field("productId")
    private String productId;

    @Field("shopId")
    private String shopId;

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

    @Field("productImages")
    @Builder.Default
    private List<ProductImage> productImages = new ArrayList<>();

    @Field("productAttributes")
    @Builder.Default
    private List<ProductAttribute> productAttributes = new ArrayList<>();

    @Field("productVariants")
    @Builder.Default
    private List<ProductVariant> productVariants = new ArrayList<>();

    public void addProductImage(ProductImage productImage) {
        productImages.add(productImage);
    }

    public void deleteProductImage(ProductImage productImage) {
        productImages.remove(productImage);
    }

    public void addProductAttribute(ProductAttribute productAttribute) {
        productAttributes.add(productAttribute);
    }

    public void addProductVariant(ProductVariant productVariant) {
        productVariants.add(productVariant);
    }

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
        @Field("productImageId")
        private String productImageId;
        @Field("url")
        private String url;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductAttribute {
        @Id
        @Field("productAttributeId")
        private String productAttributeId;
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
        @Field("productAttributeValueId")
        private String productAttributeValueId;
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
        @Field("productVariantId")
        private String productVariantId;

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
        @Field("productVariantAttributeValueId")
        private String productVariantAttributeValueId;
        @Field("productAttributeId")
        private String productAttributeId;
        @Field("productAttributeValueId")
        private String productAttributeValueId;
    }
}
