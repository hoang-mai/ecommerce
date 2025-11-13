package com.ecommerce.library.kafka.event.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CreateOrderItemEvent {
    private Long productId;
    private Long productVariantId;
    private Integer quantity;
    private BigDecimal price;
}
