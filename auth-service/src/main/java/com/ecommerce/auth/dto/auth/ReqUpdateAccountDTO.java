package com.ecommerce.auth.dto.auth;

import com.ecommerce.library.enumeration.AccountStatus;
import com.ecommerce.library.utils.MessageError;
import com.ecommerce.library.utils.validate.AccountStatusFormat;
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
