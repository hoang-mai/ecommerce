package com.ecommerce.library.kafka.event.order;

import lombok.*;

import java.util.List;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateListOrderStatusEvent {
    private Long userId;
    private List<OrderStatusEvent> orderStatusEventList;
}
