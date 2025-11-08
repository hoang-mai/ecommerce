package com.ecommerce.auth.dto.auth;

import com.ecommerce.library.utils.MessageError;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ReqLoginDTO {

    @NotBlank(message = MessageError.USERNAME_NOT_BLANK)
    private String username;

    @NotBlank(message = MessageError.PASSWORD_NOT_BLANK)
    @Size(min = 6, max = 20, message = MessageError.PASSWORD_SIZE)
    private String password;

}
