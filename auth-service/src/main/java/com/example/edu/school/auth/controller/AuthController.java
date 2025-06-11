package com.example.edu.school.auth.controller;

import com.example.edu.school.auth.dto.request.*;
import com.example.edu.school.auth.dto.request.register.ParentRegisterRequest;
import com.example.edu.school.auth.dto.request.register.RegisterRequest;
import com.example.edu.school.auth.dto.response.BaseResponse;
import com.example.edu.school.auth.dto.response.LoginResponse;
import com.example.edu.school.auth.dto.response.RegisterResponse;
import com.example.edu.school.auth.service.AuthService;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<BaseResponse<RegisterResponse>> register(@Valid @RequestBody RegisterRequest registerRequest) {
        log.info("Registering admin: {}", registerRequest);
        RegisterResponse registerResponse = authService.register(registerRequest);
        log.info("Admin registered successfully: {}", registerResponse);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(BaseResponse.<RegisterResponse>builder()
                        .statusCode(HttpStatus.CREATED.value())
                        .message("Đăng ký thành công")
                        .data(registerResponse)
                        .build());
    }
    @PostMapping("/register-parent")
    public ResponseEntity<BaseResponse<RegisterResponse>> registerParent(@Valid @RequestBody ParentRegisterRequest registerRequest, @RequestParam @JsonProperty("student_id") Long studentId){
        log.info("Registering parent: {}", registerRequest);
        RegisterResponse email = authService.registerParent(registerRequest, studentId);
        log.info("Parent registered successfully: {}", email);
        return ResponseEntity.status(HttpStatus.CREATED).body(BaseResponse.<RegisterResponse>builder()
                .statusCode(HttpStatus.CREATED.value())
                .message("Đăng ký phụ huynh thành công")
                .data(email)
                .build());
    }

    @PostMapping("/login")
    public ResponseEntity<BaseResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest loginRequest) {
        log.info("Logging in user: {}", loginRequest);
        LoginResponse loginResponse = authService.login(loginRequest);
        log.info("User logged in successfully: {}", loginResponse);
        return ResponseEntity.ok(BaseResponse.<LoginResponse>builder()
                        .statusCode(HttpStatus.OK.value())
                        .message("Đăng nhập thành công")
                        .data(loginResponse)
                        .build());
    }
    @PostMapping("/validate-access-token")
    public ResponseEntity<BaseResponse<Boolean>> validateAccessToken(@RequestBody ValidateAccessTokenRequest validateAccessTokenRequest) {
        log.info("Verifying token: {}", validateAccessTokenRequest.getAccessToken());
        boolean isValid = authService.validateAccessToken(validateAccessTokenRequest.getAccessToken());
        log.info("Token verified successfully");
        return ResponseEntity.ok(BaseResponse.<Boolean>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Xác thực thành công")
                .data(isValid)
                .build());
    }
}
