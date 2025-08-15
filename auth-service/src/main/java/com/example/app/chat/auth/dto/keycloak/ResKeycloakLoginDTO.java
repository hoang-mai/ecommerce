package com.example.app.chat.auth.dto.keycloak;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class ResKeycloakLoginDTO {
    private String accessToken;
    private int expiresIn;
    private int refreshExpiresIn;
    private String refreshToken;
    private String tokenType;
    private String sessionState;
    private String scope;

}
