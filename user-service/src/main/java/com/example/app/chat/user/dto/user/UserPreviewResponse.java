package com.example.app.chat.user.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class UserPreviewResponse {

    private Long userId;

    private String firstName;

    private String middleName;

    private String lastName;

    private String email;

    private String avatarUrl;
}
