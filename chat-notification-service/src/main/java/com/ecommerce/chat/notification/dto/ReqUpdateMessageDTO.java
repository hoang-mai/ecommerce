package com.ecommerce.chat.notification.dto;

import lombok.Getter;

@Getter
public class ReqUpdateMessageDTO {
    private Boolean isDeleted;
    private String content;
}
