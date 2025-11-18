package com.ecommerce.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PushSubscriptionDto {

    private Long pushSubscriptionId;
    private Long userId;
    private String endpoint;
    private String p256dh;
    private String auth;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

