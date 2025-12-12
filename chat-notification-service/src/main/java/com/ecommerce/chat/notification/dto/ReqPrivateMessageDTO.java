package com.ecommerce.chat.notification.dto;

import com.ecommerce.chat.notification.entity.MessageType;
import lombok.Getter;

@Getter
public class ReqPrivateMessageDTO {
    private Long receiverId;
    private String chatId;
    private String messageContent;
    private MessageType messageType;
    private Long shopId;
}
