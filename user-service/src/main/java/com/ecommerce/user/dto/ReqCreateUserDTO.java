package com.ecommerce.user.dto;


import com.ecommerce.library.enumeration.Gender;
import com.ecommerce.library.utils.MessageError;
import com.ecommerce.library.utils.validate.PhoneNumberFormat;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class ReqCreateUserDTO {

    @NotBlank(message = MessageError.USERNAME_NOT_BLANK)
    @Size(min = 3, max = 20, message = MessageError.USERNAME_SIZE)
    @Schema(description = "Username must be between 3 and 20 characters", example = "john_doe")
    private String username;

    @NotBlank(message = MessageError.PASSWORD_NOT_BLANK)
    @Size(min = 6, max = 20, message = MessageError.PASSWORD_SIZE)
    @Schema(description = "Password must be between 6 and 20 characters", example = "password123")
    private String password;


    @Size(min = 1, max = 10, message = MessageError.NAME_SIZE)
    @Schema(description = "First name must be between 1 and 10 characters", example = "John")
    private String firstName;

    @Schema(description = "Middle name is optional and can be up to 10 characters", example = "A")
    private String middleName;

    @NotBlank(message = MessageError.LAST_NAME_NOT_BLANK)
    @Size(min = 1, max = 10, message = MessageError.NAME_SIZE)
    @Schema(description = "Last name must be between 1 and 10 characters", example = "Doe")
    private String lastName;

    @Schema(description = "Gender must be one of the following: MALE, FEMALE, OTHER", example = "MALE")
    private Gender gender;

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
