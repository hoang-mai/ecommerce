package com.ecommerce.library.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum OrderStatus {
    PENDING("PENDING"),
    CONFIRMED("CONFIRMED"),
    PAID("PAID"),
    DELIVERED("DELIVERED"),
    SHIPPED("SHIPPED"),
    COMPLETED("COMPLETED"),
    RETURNED("RETURNED"),
    CANCELLED("CANCELLED");

    private final String value;
}