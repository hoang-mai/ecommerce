package com.ecommerce.notification.dto;

import com.ecommerce.notification.entity.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;
import java.time.LocalDateTime;


@Getter
@Builder
public class NotificationDto {
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final Long notificationId;
    private final Long userId;
    private final String title;
    private final String message;
    private final NotificationType notificationType;
    private final Boolean isRead;
    private final Boolean sentRealtime;
}