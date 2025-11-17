package com.ecommerce.notification.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NotificationType {

    INFO("INFO"),
    ALERT("ALERT"),
    PROMOTION("PROMOTION"),
    TRANSACTIONAL("TRANSACTIONAL"),
    ORDER_UPDATE("ORDER_UPDATE");

    private final String notificationType;
}
