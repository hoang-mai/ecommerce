package com.ecommerce.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ResInfoPreviewUserDTO {

    private Long userId;
    private String fullName;
    private String email;
    private String avatarUrl;
}
