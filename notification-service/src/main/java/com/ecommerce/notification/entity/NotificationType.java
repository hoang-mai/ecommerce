package com.ecommerce.notification.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NotificationType {
    ERROR("error"),
    SUCCESS("success"),
    INFO("info"),
    WARNING("warning");

    private final String notificationType;
}
