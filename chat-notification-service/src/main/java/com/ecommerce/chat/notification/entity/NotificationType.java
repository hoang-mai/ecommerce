package com.ecommerce.chat.notification.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NotificationType {
    PAYMENT("payment"),
    PARTIALLY_OUT_OF_STOCK("partially_out_of_stock"),
    ALL_OUT_OF_STOCK("all_out_of_stock"),
    ERROR("error"),
    SUCCESS("success"),
    INFO("info"),
    WARNING("warning");

    private final String notificationType;
}
