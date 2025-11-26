package com.ecommerce.library.kafka.event.cart;

import lombok.*;

import java.time.LocalDateTime;
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
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
