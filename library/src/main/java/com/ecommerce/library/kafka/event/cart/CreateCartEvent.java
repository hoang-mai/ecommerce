package com.ecommerce.library.kafka.event.cart;

import lombok.*;

import java.time.Instant;
import java.util.List;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateCartEvent {
    private Long cartId;
    private Long userId;
    private List<CreateCartItemEvent> createCartItemEventList;
    private Instant createdAt;
    private Instant updatedAt;
}
