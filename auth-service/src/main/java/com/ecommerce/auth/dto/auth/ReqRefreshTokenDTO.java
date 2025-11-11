package com.ecommerce.auth.dto.auth;

import com.ecommerce.library.utils.MessageError;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class ReqRefreshTokenDTO {

    @NotBlank(message = MessageError.REFRESH_TOKEN_NOT_BLANK)
    private String refreshToken;
}
