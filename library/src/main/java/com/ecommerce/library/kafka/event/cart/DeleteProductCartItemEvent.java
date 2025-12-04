package com.ecommerce.library.kafka.event.cart;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DeleteProductCartItemEvent {
    private Long cartId;
    private Long cartItemId;
    private Long productCartItemId;
    private Boolean isDeleteCartItem;
}

