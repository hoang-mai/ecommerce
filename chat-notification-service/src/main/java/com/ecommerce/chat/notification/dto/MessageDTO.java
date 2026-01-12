package com.ecommerce.chat.notification.dto;

import com.ecommerce.chat.notification.entity.MessageType;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.List;

@Builder
@Getter
public class MessageDTO {
    @JsonProperty("messageId")
    private String _id;
    private String chatId;
    private String senderId;
    private String shopId;
    private MessageType messageType;
    private String messageContent;
    private String replyToMessageId;
    private Boolean isEdited;
    private Boolean isDeleted;
    private Instant deletedAt;
    private Instant createdAt;
    private Instant updatedAt;
    private List<String> readBy;
}