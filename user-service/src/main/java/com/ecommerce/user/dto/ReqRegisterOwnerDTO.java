package com.ecommerce.user.dto;

import com.ecommerce.library.utils.MessageError;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqRegisterOwnerDTO {

    @NotBlank(message = MessageError.VERIFICATION_CODE_NOT_BLANK)
    @Schema(description = "Verification code sent to the user for owner registration", example = "ABC123")
    private String verificationCode;


    @NotBlank(message = MessageError.ACCOUNT_NUMBER_NOT_BLANK)
    @Schema(description = "Bank account number of the user", example = "123456789")
    private String accountNumber;

    @NotBlank(message = MessageError.BANK_NAME_NOT_BLANK)
    @Schema(description = "Name of the bank associated with the account", example = "Bank of E-commerce")
    private String bankName;
}
