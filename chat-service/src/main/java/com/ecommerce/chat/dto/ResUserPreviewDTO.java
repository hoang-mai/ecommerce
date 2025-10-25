package com.ecommerce.chat.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ResUserPreviewDTO {
    private Long senderId;
    private String avatarUrl;
}
