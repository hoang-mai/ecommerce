package com.example.app.chat.chat.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ResUserPreviewDTO {
    private Long senderId;
    private String avatarUrl;
}
