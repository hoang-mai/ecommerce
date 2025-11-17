package com.ecommerce.library.kafka.event.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CreateOrderItemEvent {
    private Long orderItemId;
    private Long productId;
    private String productName;
    private List<CreateProductOrderItemEvent> createProductOrderItemEvents;
    private List<ProductImage> productImageList;
}
