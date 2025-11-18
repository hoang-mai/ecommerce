package com.ecommerce.read.entity;

import com.ecommerce.library.enumeration.OrderStatus;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.math.BigDecimal;
import java.util.List;

@Document(collection = "order_views")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderView {

    @Id
    @Field(name = "order_id")
    private String orderId;

    @Field(name = "user_id")
    private Long userId;

    @Field(name = "order_status")
    private OrderStatus orderStatus;

    @Field(name = "reason")
    private String reason;

    @Field(name = "total_price")
    private BigDecimal totalPrice;

    @Field(name = "payment_id")
    private Long paymentId;

    @Field(name = "address")
    private String address;

    @Field(name = "phone_number")
    private String phoneNumber;

    @CreatedDate
    @Field(name = "created_at")
    private Long createdAt;

    @LastModifiedDate
    @Field(name = "updated_at")
    private Long updatedAt;

    @Field(name = "order_items")
    private List<OrderItem> orderItems;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItem {

        @Id
        @Field(name = "order_item_id")
        private Long orderItemId;

        @Field(name = "product_id")
        private Long productId;

        @Field(name = "productName")
        private String productName;

        @Field(name = "product")
        private List<ProductImage> productImageList;


        @Field(name = "product_variants")
        private List<ProductVariant> productVariants;
    }

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ProductVariant {

        @Id
        @Field(name = "product_variant_id")
        private Long productVariantId;

        @Field(name = "price")
        private BigDecimal price;

        @Field(name = "quantity")
        private Integer quantity;

        @Field(name = "product_attributes")
        private List<ProductAttribute> productAttributes;

    }

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ProductAttribute {

        @Field(name = "attribute_name")
        private String attributeName;

        @Field(name = "attribute_value")
        private String attributeValue;

    }

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ProductImage {

        @Id
        @Field(name = "product_image_id")
        private Long productImageId;

        @Field(name = "image_url")
        private String imageUrl;
    }
}

