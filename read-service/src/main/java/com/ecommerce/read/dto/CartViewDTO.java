package com.ecommerce.read.dto;


import com.ecommerce.read.entity.FlashSaleProductView;
import com.ecommerce.read.entity.ProductView;
import com.ecommerce.read.entity.ShopView;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.Instant;
import java.util.List;
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartViewDTO {
    @JsonProperty("cartId")
    private String _id;
    private List<CartItemViewDTO> cartItems ;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CartItemViewDTO {
        @JsonProperty("cartItemId")
        private String _id;
        private ShopView shopView;
        private List<ProductCartItemViewDTO> productCartItems;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductCartItemViewDTO {
        @JsonProperty("productCartItemId")
        private String _id;

        private ProductView productView;

        private String productVariantId;

        private Integer quantity;

        private List<FlashSaleProductView> flashSaleProductView;

    }
}
