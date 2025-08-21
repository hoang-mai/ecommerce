package com.example.app.chat.chat.dto;

import com.example.app.chat.chat.entity.MessageType;
import lombok.Getter;

@Getter
public class ReqPrivateMessageDTO {
    private Long receiverId;
    private String chatId;
    private String messageContent;
    private MessageType messageType;
}
