package com.ecommerce.order.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartDTO {

    private Long cartId;
    private Long userId;

    @Builder.Default
    private List<CartItemDTO> cartItems = new ArrayList<>();

    private BigDecimal totalAmount;

}

