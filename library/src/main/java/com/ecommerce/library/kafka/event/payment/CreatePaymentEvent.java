package com.ecommerce.library.kafka.event.payment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CreatePaymentEvent {
    private Long userId;
    private Boolean isPartiallyOutOfStock;
    private Boolean isAllOutOfStock;
    private String paymentUrl;
}
