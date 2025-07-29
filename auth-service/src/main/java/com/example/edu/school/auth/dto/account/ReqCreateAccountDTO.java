package com.example.edu.school.auth.dto.account;

import com.example.edu.school.library.enumeration.Gender;
import com.example.edu.school.library.enumeration.Role;
import com.example.edu.school.library.utils.MessageError;
import com.example.edu.school.library.utils.validate.PhoneNumberFormat;
import com.example.edu.school.library.utils.validate.RoleFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class ReqCreateAccountDTO {

    @NotBlank(message = MessageError.PASSWORD_NOT_BLANK)
    @Size(min = 6, max = 20, message = MessageError.PASSWORD_SIZE)
    @Schema(description = "Password must be between 6 and 20 characters", example = "123456")
    private String password;

    @RoleFormat
    @Schema(description = "Role must be one of the following: STUDENT, TEACHER, ADMIN", example = "STUDENT")
    private Role role;

    @NotBlank(message = MessageError.FIRST_NAME_NOT_BLANK)
    @Size(min = 1, max = 10, message = MessageError.FIRST_NAME_SIZE)
    @Schema(description = "First name must be between 1 and 10 characters", example = "John")
    private String firstName;

    @Schema(description = "Middle name is optional and can be up to 10 characters", example = "A")
    private String middleName;

    @NotBlank(message = MessageError.LAST_NAME_NOT_BLANK)
    @Size(min = 1, max = 10, message = MessageError.LAST_NAME_SIZE)
    @Schema(description = "Last name must be between 1 and 10 characters", example = "Doe")
    private String lastName;

    @PhoneNumberFormat
    @Schema(description = "Phone number must be in the format 0123456789", example = "0123456789")
    private String phoneNumber;

    @Schema(description = "Address is optional and can be up to 100 characters", example = "123 Main St, City, Country")
    private String address;

    @Schema(description = "Gender must be one of the following: MALE, FEMALE, OTHER", example = "FEMALE")
    private Gender gender;

    @Schema(description = "Date of birth in the format YYYY-MM-DD", example = "2000-01-01")
    private LocalDate dateOfBirth;
}
