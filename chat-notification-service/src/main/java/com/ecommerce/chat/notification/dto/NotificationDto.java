package com.ecommerce.chat.notification.dto;

import com.ecommerce.chat.notification.entity.NotificationType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Builder
public class NotificationDto {
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final String notificationId;
    private final Long userId;
    private final String title;
    private final String message;
    private final Map<String, Object> data;
    private final NotificationType notificationType;
    private final Boolean isRead;
    private final Boolean sentRealtime;
}