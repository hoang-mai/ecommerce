package com.ecommerce.library.kafka.event.cart;

import jakarta.persistence.*;
import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateProductCartItemEvent {
    private Long productId;
    private Long productCartItemId;
    private Long productVariantId;
    private Integer quantity;
}
