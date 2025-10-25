package com.ecommerce.auth.dto.auth;

import com.ecommerce.library.utils.MessageError;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqLoginDTO {

    @NotBlank(message = MessageError.USERNAME_NOT_BLANK)
    private String username;

    @NotBlank(message = MessageError.PASSWORD_NOT_BLANK)
    private String password;

}
