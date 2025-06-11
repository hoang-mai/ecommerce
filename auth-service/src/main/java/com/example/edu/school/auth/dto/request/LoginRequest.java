package com.example.edu.school.auth.dto.request;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class LoginRequest {
    @Schema(description = "email",example = "Hoang.MA0123@school.edu")
    @NotBlank(message = "Email không được để trống")
    private String email;

    @Schema(description = "password",example = "123456")
    @NotBlank(message = "Mật khẩu phải có ít nhất 6 ký tự")
    private String password;
}
