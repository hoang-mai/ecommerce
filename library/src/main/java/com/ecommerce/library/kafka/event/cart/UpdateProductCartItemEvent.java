package com.ecommerce.library.kafka.event.cart;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateProductCartItemEvent {
    private Long cartId;
    private Long cartItemId;
    private Long productCartItemId;
    private Integer quantity;
}
