package com.ecommerce.library.kafka.event.order;

import com.ecommerce.library.enumeration.OrderStatus;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateOrderEvent {
    private Long orderId;
    private Long userId;
    private OrderStatus orderStatus;
    private BigDecimal totalPrice;
    private String address;
    private String phoneNumber;
    private String reason;
    private List<CreateOrderItemEvent> createOrderItemEventList;

}
