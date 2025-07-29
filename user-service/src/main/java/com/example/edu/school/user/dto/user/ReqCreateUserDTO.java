package com.example.edu.school.user.dto.user;

import java.time.LocalDate;

import com.example.edu.school.library.enumeration.Gender;
import com.example.edu.school.library.enumeration.Role;
import com.example.edu.school.library.utils.MessageError;
import com.example.edu.school.library.utils.validate.PhoneNumberFormat;
import com.example.edu.school.library.utils.validate.RoleFormat;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "role", visible = true, defaultImpl = ReqCreateUserDTO.class)
@JsonSubTypes({
        @Type(value = StudentReqCreateUserDTO.class, name = "STUDENT"),
        @Type(value = TeacherReqCreateUserDTO.class, name = "TEACHER"),
        @Type(value = ParentReqCreateUserDTO.class, name = "PARENT")
})
@Getter
public class ReqCreateUserDTO {

    @NotBlank(message = MessageError.ACCOUNT_ID_NOT_BLANK)
    @Schema(description = "Account ID must be between 1 and 20 characters", example = "john_doe_123")
    private String accountId;

    @Email(message = MessageError.INVALID_EMAIL)
    @NotBlank(message = MessageError.EMAIL_NOT_BLANK)
    private String email;


    @NotBlank(message = MessageError.FIRST_NAME_NOT_BLANK)
    @Schema(description = "First name must be between 1 and 10 characters", example = "John")
    private String firstName;

    @Schema(description = "Middle name is optional and can be up to 10 characters", example = "A")
    private String middleName;

    @NotBlank(message = MessageError.LAST_NAME_NOT_BLANK)
    @Schema(description = "Last name must be between 1 and 10 characters", example = "Doe")
    private String lastName;

    @PhoneNumberFormat
    @Schema(description = "Phone number must be in the format 0123456789", example = "0123456789")
    private String phoneNumber;

    @RoleFormat
    @Schema(description = "Role must be one of the following: STUDENT, TEACHER, ADMIN", example = "STUDENT")
    private Role role;

    @Schema(description = "Gender must be one of the following: MALE, FEMALE, OTHER", example = "MALE")
    private Gender gender;

    @Schema(description = "Date of birth in the format YYYY-MM-DD", example = "2000-01-01")
    private LocalDate dateOfBirth;

    @Schema(description = "Address is optional and can be up to 100 characters", example = "123 Main St, City, Country")
    private String address;
}
