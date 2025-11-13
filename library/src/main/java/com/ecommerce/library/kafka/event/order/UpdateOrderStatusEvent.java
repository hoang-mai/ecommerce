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
public class UpdateOrderStatusEvent {
    private Long orderId;
    private OrderStatus orderStatus;
}
