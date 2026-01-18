package com.ecommerce.library.kafka.event.order;

import com.ecommerce.library.enumeration.OrderStatus;
import lombok.*;



@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderStatusEvent {
    private Long userId;
    private Long orderId;
    private String orderCode;
    private OrderStatus orderStatus;
    private String reason;
    private Long ownerId;
}
