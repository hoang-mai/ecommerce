package com.example.app.chat.user.dto;

import java.time.LocalDate;

import com.example.app.chat.library.enumeration.Gender;
import com.example.app.chat.library.utils.MessageError;
import com.example.app.chat.library.utils.validate.PhoneNumberFormat;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    @Email
    @NotBlank(message = MessageError.EMAIL_NOT_BLANK)
    @Schema(description = "Email must be a valid email address", example = "john.d@gmail.com")
    private String email;

    @Size(min = 1, max = 10, message = MessageError.NAME_SIZE)
    @Schema(description = "First name must be between 1 and 10 characters", example = "John")
    private String firstName;

    @Size(min = 1, max = 10, message = MessageError.NAME_SIZE)
    @Schema(description = "Middle name is optional and can be up to 10 characters", example = "A")
    private String middleName;

    @NotBlank(message = MessageError.LAST_NAME_NOT_BLANK)
    @Size(min = 1, max = 10, message = MessageError.NAME_SIZE)
    @Schema(description = "Last name must be between 1 and 10 characters", example = "Doe")
    private String lastName;

    @PhoneNumberFormat
    @Schema(description = "Phone number must be in the format 0123456789", example = "0123456789")
    private String phoneNumber;

    @Schema(description = "Gender must be one of the following: MALE, FEMALE, OTHER", example = "MALE")
    private Gender gender;

    @Schema(description = "Date of birth in the format YYYY-MM-DD", example = "2000-01-01")
    private LocalDate dateOfBirth;

    @Schema(description = "Address is optional and can be up to 100 characters", example = "123 Main St, City, Country")
    private String address;
}
