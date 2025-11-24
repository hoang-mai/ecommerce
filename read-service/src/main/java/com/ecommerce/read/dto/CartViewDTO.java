package com.ecommerce.read.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDateTime;
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
        private ProductViewDTO productViewDTO;
        private List<ProductCartItemViewDTO> productCartItems;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductCartItemViewDTO {
        @JsonProperty("productCartItemId")
        private String _id;

        private String productVariantId;

        private Integer quantity;

    }
}
