package com.ecommerce.library.enumeration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PaymentStatus {
    PENDING("PENDING"),
    SUCCESS("SUCCESS"),
    FAILED("FAILED"),
    CANCELLED("CANCELLED"),
    REFUNDED("REFUNDED");

    private final String paymentStatus;
}
