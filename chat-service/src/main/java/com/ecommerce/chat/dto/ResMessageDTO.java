package com.ecommerce.chat.dto;

import com.ecommerce.chat.entity.MessageType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
public class ResMessageDTO {
    private String messageId;
    private String chatMemberId;
    private MessageType messageType;
    private String content;
    private boolean isDeleted;
    private LocalDateTime timestamp;
    private boolean isUpdated;
    private ResUserPreviewDTO sender;
}