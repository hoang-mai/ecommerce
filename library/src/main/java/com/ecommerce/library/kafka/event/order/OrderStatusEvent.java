package com.ecommerce.library.kafka.event.order;

import com.ecommerce.library.enumeration.OrderStatus;
import lombok.*;

import java.util.List;


@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderStatusEvent {
    private Long userId;
    private Long orderId;
    private OrderStatus orderStatus;
    private String reason;
}
