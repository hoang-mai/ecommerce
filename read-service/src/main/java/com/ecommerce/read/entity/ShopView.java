package com.ecommerce.read.entity;

import com.ecommerce.library.enumeration.ShopStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.math.BigDecimal;

@Document(collection = "shop_views")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ShopView extends BaseEntity {
    @Id
    @Field(name = "_id")
    @JsonProperty("shopId")
    private String _id;

    @Field(name = "ownerId")
    private String ownerId;

    @Field(name = "shopName")
    private String shopName;

    @Field(name = "description")
    private String description;

    @Field(name = "logoUrl")
    private String logoUrl;

    @Field(name = "bannerUrl")
    private String bannerUrl;

    @Field(name = "shopStatus")
    private ShopStatus shopStatus;

    @Field(name = "province")
    private String province;

    @Field(name = "ward")
    private String ward;

    @Field(name = "detail")
    private String detail;

    @Field(name = "phoneNumber")
    private String phoneNumber;

    @Field("totalProducts")
    private Long totalProducts;

    @Field("activeProducts")
    private Long activeProducts;

    @Field("totalSold")
    private Long totalSold;

    @Field("totalRevenue")
    private BigDecimal totalRevenue;

    @Field("averageRating")
    private Double averageRating;


}
