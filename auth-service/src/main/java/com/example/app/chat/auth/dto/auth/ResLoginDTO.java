package com.example.app.chat.auth.dto.auth;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ResLoginDTO {
    private String accessToken;
    private Integer expiresIn;
    private String refreshToken;
    private Integer refreshExpiresIn;
    private String tokenType;
    private String sessionState;
    private String scope;
}
