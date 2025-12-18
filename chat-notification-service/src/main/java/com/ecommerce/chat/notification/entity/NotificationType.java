package com.ecommerce.chat.notification.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NotificationType {
    PAYMENT("payment"),
    ERROR("error"),
    SUCCESS("success"),
    INFO("info"),
    WARNING("warning");

    private final String notificationType;
}
