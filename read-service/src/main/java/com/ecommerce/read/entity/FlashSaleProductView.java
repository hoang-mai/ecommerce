package com.ecommerce.read.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "flash_sale_product_views")
public class FlashSaleProductView extends BaseEntity {

    @Id
    @JsonProperty("flashSaleProductId")
    private String flashSaleProductId;

    @Field("flashSaleCampaignId")
    private String flashSaleCampaignId;

    @Field("ownerId")
    private String ownerId;

    @Field("shopId")
    private String shopId;

    @Field("productId")
    private String productId;

    @Field("productVariantId")
    private String productVariantId;

    @Field("productName")
    private String productName;

    @Field("flashSaleCampaignName")
    private String flashSaleCampaignName;

    @Field("startTime")
    private Instant startTime;

    @Field("endTime")
    private Instant endTime;

    @Field("originalPrice")
    private BigDecimal originalPrice;

    @Field("discountPercentage")
    private Double discountPercentage;

    @Field("salePrice")
    private BigDecimal salePrice;

    @Field("totalQuantity")
    private Integer totalQuantity;

    @Field("soldQuantity")
    private Integer soldQuantity;

    @Field("totalRevenue")
    private BigDecimal totalRevenue;

    @Field("maxQuantityPerUser")
    private Integer maxQuantityPerUser;

    @Field("isSoldOut")
    private Boolean isSoldOut;

    @Field("score")
    private Double score;

    @Field("totalSold")
    private Integer totalSold;

    @Field("rating")
    private Double rating;


    @Field("productImages")
    @Builder.Default
    private List<ProductView.ProductImage> productImages = new ArrayList<>();

    @Field("productAttributes")
    @Builder.Default
    private List<ProductAttribute> productAttributes = new ArrayList<>();

    public BigDecimal calculateSalePrice() {
        if (originalPrice == null || discountPercentage == null) {
            return BigDecimal.ZERO;
        }
        BigDecimal discountAmount = originalPrice.multiply(BigDecimal.valueOf(discountPercentage)).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        return originalPrice.subtract(discountAmount);
    }

    public BigDecimal calculateTotalRevenue() {
        if (salePrice == null || soldQuantity == null) {
            return BigDecimal.ZERO;
        }
        return salePrice.multiply(BigDecimal.valueOf(soldQuantity));
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
        @Field("attributeName")
        private String attributeName;

        @Field("attributeValue")
        private String attributeValue;
    }
}
