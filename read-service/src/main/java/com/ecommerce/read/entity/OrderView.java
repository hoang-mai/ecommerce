package com.ecommerce.read.entity;

import com.ecommerce.library.enumeration.OrderStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "order_views")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class OrderView extends BaseEntity {

    @Id
    @Field(name = "_id")
    @JsonProperty("orderId")
    private String _id;

    @Field(name = "userId")
    private String userId;

    @Field(name = "ownerId")
    private String ownerId;

    @Field(name = "shopId")
    private String shopId;

    @Field(name = "shopName")
    private String shopName;

    @Field(name = "shopLogoUrl")
    private String shopLogoUrl;

    @Field(name = "orderStatus")
    private OrderStatus orderStatus;

    @Field(name = "reason")
    private String reason;

    @Field(name = "totalPrice")
    private BigDecimal totalPrice;

    @Field(name = "paymentId")
    private String paymentId;

    @Field(name = "receiverName")
    private String receiverName;

    @Field(name = "address")
    private String address;

    @Field(name = "note")
    private String note;

    @Field(name = "phoneNumber")
    private String phoneNumber;

    @Field(name = "orderItems")
    @Builder.Default
    private List<OrderItem> orderItems = new ArrayList<>();

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItem {

        @Id
        @Field(name = "_id")
        @JsonProperty("orderItemId")
        private String _id;

        @Field(name = "productId")
        private String productId;

        @Field(name = "productName")
        private String productName;

        @Field(name = "productImageUrl")
        private String productImageUrl;

        @Field(name = "productVariantId")
        private String productVariantId;

        @Field(name = "price")
        private BigDecimal price;

        @Field(name = "quantity")
        private Integer quantity;

        @Field(name = "totalPrice")
        private BigDecimal totalPrice;

        @Field(name = "totalDiscount")
        private BigDecimal totalDiscount;

        @Field(name = "totalFinalPrice")
        private BigDecimal totalFinalPrice;


        @Field(name = "productAttributes")
        @Builder.Default
        private List<ProductAttribute> productAttributes = new ArrayList<>();

    }

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ProductAttribute {

        @Field(name = "attributeName")
        private String attributeName;

        @Field(name = "attributeValue")
        private String attributeValue;

    }
}
