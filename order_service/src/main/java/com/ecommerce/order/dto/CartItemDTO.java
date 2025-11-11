package com.ecommerce.order.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItemDTO {

    private Long cartItemId;
    private Long productId;
    private Long productVariantId;
    private Integer quantity;
    private BigDecimal price;
    private BigDecimal subtotal;
    private Long shopId;

    public BigDecimal getSubtotal() {
        if (price != null && quantity != null) {
            return price.multiply(BigDecimal.valueOf(quantity));
        }
        return BigDecimal.ZERO;
    }
}

