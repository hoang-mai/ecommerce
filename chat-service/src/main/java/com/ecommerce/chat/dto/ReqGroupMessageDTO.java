package com.ecommerce.chat.dto;

import com.ecommerce.chat.entity.MessageType;
import lombok.Getter;

@Getter
public class ReqGroupMessageDTO {
    private String chatId;
    private String messageContent;
    private MessageType messageType;
}
