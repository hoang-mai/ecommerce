package com.ecommerce.library.kafka.event.order;

import com.ecommerce.library.enumeration.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CreateOrderEvent {
    private Long orderId;
    private Long userId;
    private OrderStatus orderStatus;
    private BigDecimal totalPrice;
    private String address;
    private String phoneNumber;
    private List<CreateOrderItemEvent> createOrderItemEventList;

}
