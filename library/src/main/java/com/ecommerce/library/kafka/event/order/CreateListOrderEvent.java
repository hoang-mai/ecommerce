package com.ecommerce.library.kafka.event.order;

import lombok.*;

import java.util.List;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateListOrderEvent {
    private Long userId;
    private List<CreateOrderEvent> createOrderEventList;
}
