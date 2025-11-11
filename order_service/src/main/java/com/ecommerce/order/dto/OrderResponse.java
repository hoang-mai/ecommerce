package com.ecommerce.order.dto;

import com.ecommerce.library.enumeration.OrderStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {

    private Long orderId;
    private Long userId;
    private OrderStatus status;
    private BigDecimal totalPrice;
    private Long paymentId;
    private UUID shippingAddressId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Builder.Default
    private List<OrderItemResponse> items = new ArrayList<>();
}

