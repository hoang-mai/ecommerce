package com.ecommerce.library.kafka.event.cart;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DeleteCartItemEvent {
    private Long cartId;
    private Long cartItemId;
    private Boolean isDeletedAllItems;
}
