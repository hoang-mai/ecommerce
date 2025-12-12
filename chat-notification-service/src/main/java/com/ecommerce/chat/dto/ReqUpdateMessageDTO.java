package com.ecommerce.chat.dto;

import lombok.Getter;

@Getter
public class ReqUpdateMessageDTO {
    private Boolean isDeleted;
    private String content;
}
