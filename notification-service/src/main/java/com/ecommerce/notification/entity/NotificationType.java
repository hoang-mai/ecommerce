package com.ecommerce.notification.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NotificationType {
    PROMOTION("PROMOTION"),
    ORDER_UPDATE("ORDER_UPDATE");

    private final String notificationType;
}
