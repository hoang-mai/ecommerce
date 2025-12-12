package com.ecommerce.chat.dto;

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

    private String pushSubscriptionId;
    private Long userId;
    private String endpoint;
    private String p256dh;
    private String auth;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

