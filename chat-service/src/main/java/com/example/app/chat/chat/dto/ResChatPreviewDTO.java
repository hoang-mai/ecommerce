package com.example.app.chat.chat.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ResChatPreviewDTO {
    private String chatId;
    private String chatName;
    private String chatImageUrl;
    private ResMessageDTO lastMessage;
}
