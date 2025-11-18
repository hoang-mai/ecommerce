package com.ecommerce.library.kafka.event.order;

import lombok.*;

import java.util.List;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateOrderItemEvent {
    private Long orderItemId;
    private Long productId;
    private String productName;
    private List<CreateProductOrderItemEvent> createProductOrderItemEvents;
    private List<CreateProductImage> createProductImageList;
}
