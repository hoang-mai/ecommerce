package com.ecommerce.library.kafka.event.cart;

import lombok.*;

import java.util.List;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateCartItemEvent {
    private Long cartItemId;
    private Long shopId;
    private List<CreateProductCartItemEvent> createProductCartItemEvents;
}
