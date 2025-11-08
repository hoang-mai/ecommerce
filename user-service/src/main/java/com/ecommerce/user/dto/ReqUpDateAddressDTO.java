package com.ecommerce.user.dto;

import com.ecommerce.library.utils.MessageError;
import com.ecommerce.library.utils.validate.PhoneNumberFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class ReqUpDateAddressDTO {
    @NotBlank(message = MessageError.RECEIVER_NAME_NOT_BLANK)
    @Schema(description = "Receiver's name for the address", example = "John Doe")
    private String receiverName;

    @NotBlank(message = MessageError.PROVINCE_NOT_BLANK)
    @Schema(description = "Province for the address", example = "California")
    private String province;

    @NotBlank(message = MessageError.WARD_NOT_BLANK)
    @Schema(description = "Ward for the address", example = "Los Angeles")
    private String ward;

    @NotBlank(message = MessageError.DETAIL_NOT_BLANK)
    @Schema(description = "Detailed address information", example = "1234 Elm Street, Apt 56")
    private String detail;

    @NotBlank(message = MessageError.PHONE_NUMBER_NOT_BLANK)
    @PhoneNumberFormat
    @Schema(description = "Phone number in valid format", example = "+1234567890")
    private String phoneNumber;

}
