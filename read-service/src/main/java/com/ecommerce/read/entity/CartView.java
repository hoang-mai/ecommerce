package com.ecommerce.read.entity;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "cart_views")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class CartView extends BaseEntity {

    @Id
    @Field("_id")
    private String _id;

    @Field("userId")
    private String userId;

    @Builder.Default
    @Field("cartItems")
    private List<CartItem> cartItems = new ArrayList<>();

    public void addCartItem(CartItem cartItem) {
        cartItems.add(cartItem);
    }

    public void removeCartItem(CartItem cartItem) {
        cartItems.remove(cartItem);
    }

    public void clearCart() {
        cartItems.clear();
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CartItem {
        @Id
        @Field("_id")
        private String _id;

        @Field("productId")
        private String productId;

        @Builder.Default
        @Field("productCartItems")
        private List<ProductCartItem> productCartItems = new ArrayList<>();

        public void addProductCartItem(ProductCartItem productCartItem) {
            productCartItems.add(productCartItem);
        }
    }
    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductCartItem{
        @Id
        @Field("_id")
        private String _id;

        @Field("productVariantId")
        private String productVariantId;

        @Field("quantity")
        private Integer quantity;

    }
}
