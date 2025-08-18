package com.example.app.chat.auth.dto.auth;

import com.example.app.chat.library.enumeration.AccountStatus;
import com.example.app.chat.library.utils.MessageError;
import com.example.app.chat.library.utils.validate.AccountStatusFormat;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ReqUpdateAccountDTO {
    @AccountStatusFormat
    private AccountStatus accountStatus;

    @Pattern(
            regexp = "^.{6,20}$",
            message = MessageError.PASSWORD_SIZE
    )
    private String password;
}
