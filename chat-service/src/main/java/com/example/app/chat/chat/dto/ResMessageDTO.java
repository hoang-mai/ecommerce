package com.example.app.chat.chat.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
public class ResMessageDTO {
    private String messageId;
    private String chatMemberId;
    private String nickName;
    private String messageType;
    private String content;
    private LocalDateTime timestamp;
    private ResUserPreviewDTO sender;
}