package com.example.edu.school.auth.client;

import com.example.edu.school.auth.config.FeignConfig;
import com.example.edu.school.auth.dto.request.register.ParentRegisterRequest;
import com.example.edu.school.auth.dto.request.register.RegisterRequest;
import com.example.edu.school.auth.dto.response.BaseResponse;

import com.example.edu.school.auth.dto.response.UserCredentialResponse;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(name="user-service",configuration = FeignConfig.class)
public interface UserClient {

    @GetMapping("/api/user/login/{email}")
    ResponseEntity<BaseResponse<UserCredentialResponse>> getUserByEmail(@PathVariable String email);

    @PostMapping("/api/user/register")
    ResponseEntity<BaseResponse<String>> register(@RequestBody RegisterRequest registerRequest);

    @PostMapping("/api/user/register-parent")
    ResponseEntity<BaseResponse<String>> registerParent(@RequestBody ParentRegisterRequest registerRequest, @RequestParam Long studentId);

}


