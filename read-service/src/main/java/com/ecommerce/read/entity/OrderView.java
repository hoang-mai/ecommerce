package com.ecommerce.read.entity;

import com.ecommerce.library.enumeration.OrderStatus;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
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
    private String _id;

    @Field(name = "userId")
    private String userId;

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
        private String _id;

        @Field(name = "productId")
        private String productId;

        @Field(name = "productName")
        private String productName;

        @Field(name = "product")
        @Builder.Default
        private List<ProductImage> productImageList = new ArrayList<>();


        @Field(name = "productVariants")
        @Builder.Default
        private List<ProductVariant> productVariants = new ArrayList<>();
    }

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ProductVariant {

        @Id
        @Field(name = "_id")
        private String _id;

        @Field(name = "price")
        private BigDecimal price;

        @Field(name = "quantity")
        private Integer quantity;

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

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ProductImage {

        @Id
        @Field(name = "_id")
        private String _id;

        @Field(name = "imageUrl")
        private String imageUrl;
    }
}
