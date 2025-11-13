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
public class CreateOrderEvent {
    private Long orderId;
    private List<CreateOrderItemEvent> createOrderItemEventList;

}
