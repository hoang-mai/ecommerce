package com.ecommerce.library.kafka.event.order;

import com.ecommerce.library.enumeration.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OderStatusEvent {

    private Long orderId;
    private Long userId;
    private OrderStatus orderStatus;
}
