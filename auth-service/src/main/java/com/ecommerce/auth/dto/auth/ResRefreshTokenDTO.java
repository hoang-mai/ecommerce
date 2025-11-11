package com.ecommerce.auth.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ResRefreshTokenDTO {
    private String accessToken;

    private int expiresIn;

    private String refreshToken;

    private int refreshExpiresIn;

    private String tokenType;

    private int notBeforePolicy;

    private String sessionState;

    private String scope;
}
