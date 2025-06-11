package com.example.edu.school.user.dto.request.register;

import java.time.LocalDate;

import com.example.edu.school.user.model.Gender;
import com.example.edu.school.user.model.Role;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

/*
Chỉ admin và assistant có thể tạo tài khoản
*/

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "role", visible = true,defaultImpl = RegisterRequest.class)
@JsonSubTypes({
        @Type(value = StudentRegisterRequest.class, name = "STUDENT"),
        @Type(value = TeacherRegisterRequest.class, name = "TEACHER"),
        @Type(value = ParentRegisterRequest.class, name = "PARENT")
})
@Getter
public class RegisterRequest {

    @Size(min = 6, message = "Mật khẩu phải có ít nhất 6 ký tự")
    @NotBlank(message = "Mật khẩu không được để trống")
    @JsonProperty("password")
    @Schema(description = "Mật khẩu của người dùng", example = "123456")
    private String password;

    @NotBlank(message = "Họ không được để trống")
    @JsonProperty("first_name")
    @Schema(description = "Họ của người dùng", example = "Nguyễn Văn")
    private String firstName;

    @JsonProperty("middle_name")
    @Schema(description = "Tên đệm của người dùng", example = "A")
    private String middleName;

    @NotBlank(message = "Tên không được để trống")
    @JsonProperty("last_name")
    @Schema(description = "Tên của người dùng", example = "Nguyễn")
    private String lastName;

    @NotNull(message = "Vai trò không được để trống")
    @JsonProperty("role")
    @Schema(description = "Vai trò của người dùng", example = "STUDENT")
    private Role role;

    @JsonProperty("gender")
    @Schema(description = "Giới tính của người dùng", example = "MALE")
    private Gender gender;

    @JsonProperty("date_of_birth")
    @Schema(description = "Ngày sinh của người dùng", example = "2000-01-01")
    private LocalDate dateOfBirth;
}

