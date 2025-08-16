package com.example.app.chat.auth.dto.auth;

import com.example.app.chat.library.utils.MessageError;
import jakarta.validation.constraints.Email;
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
