package com.ecommerce.library.kafka.event.order;

import com.ecommerce.library.enumeration.OrderStatus;

import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateOrderEvent {
    private Long orderId;
    private String orderCode;
    private Long userId;
    private Long ownerId;
    private Long shopId;
    private String shopName;
    private String shopLogoUrl;
    private OrderStatus orderStatus;
    private String reason;
    private BigDecimal totalPrice;
    private String receiverName;
    private String address;
    private String phoneNumber;
    private List<CreateOrderItemEvent> createOrderItemEventList;
    private Instant createdAt;
    private Instant updatedAt;
    private Long cartItemId;
    private String note;
}
