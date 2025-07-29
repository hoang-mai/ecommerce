package com.example.edu.school.auth.dto.auth;

import com.example.edu.school.library.utils.MessageError;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqLoginDTO {

    @Email(message = MessageError.INVALID_EMAIL)
    private String email;

    @NotBlank(message = MessageError.PASSWORD_NOT_BLANK)
    private String password;

}
