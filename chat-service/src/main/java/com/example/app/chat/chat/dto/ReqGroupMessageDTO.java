package com.example.app.chat.chat.dto;

import com.example.app.chat.chat.entity.MessageType;
import lombok.Getter;

@Getter
public class ReqGroupMessageDTO {
    private String chatId;
    private String messageContent;
    private MessageType messageType;
}
