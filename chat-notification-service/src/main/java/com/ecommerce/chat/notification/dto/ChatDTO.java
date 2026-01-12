package com.ecommerce.chat.notification.dto;

import com.ecommerce.chat.notification.entity.ChatType;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
@Builder
public class ChatDTO {
    @JsonProperty("chatId")
    private String _id;
    private ChatType chatType;
    private String shopId;
    private String shopName;
    private String shopLogoUrl;
    private String userId;
    private String fullName;
    private String avatarUrl;
    private Instant createdAt;
    private Instant updatedAt;
    private MessageDTO lastMessage;
}
