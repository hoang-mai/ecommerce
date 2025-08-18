package com.example.app.chat.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ResInfoPreviewUserDTO {

    private Long userId;
    private String firstName;
    private String middleName;
    private String lastName;
    private String email;
    private String avatarUrl;
}
