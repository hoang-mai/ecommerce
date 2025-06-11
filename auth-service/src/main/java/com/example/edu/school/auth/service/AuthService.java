package com.example.edu.school.auth.service;

import com.example.edu.school.auth.dto.request.LoginRequest;
import com.example.edu.school.auth.dto.request.register.ParentRegisterRequest;
import com.example.edu.school.auth.dto.request.register.RegisterRequest;
import com.example.edu.school.auth.dto.response.LoginResponse;
import com.example.edu.school.auth.dto.response.RegisterResponse;

public interface AuthService {
    RegisterResponse register(RegisterRequest registerRequest);


    LoginResponse login(LoginRequest loginRequest);

    boolean validateAccessToken(String token);

    RegisterResponse registerParent(ParentRegisterRequest registerRequest, Long studentId);
}
