package com.example.edu.school.auth.service;

import com.example.edu.school.auth.client.UserClient;
import com.example.edu.school.auth.dto.request.LoginRequest;
import com.example.edu.school.auth.dto.request.register.ParentRegisterRequest;
import com.example.edu.school.auth.dto.request.register.RegisterRequest;
import com.example.edu.school.auth.dto.request.register.StudentRegisterRequest;
import com.example.edu.school.auth.dto.response.BaseResponse;
import com.example.edu.school.auth.dto.response.LoginResponse;
import com.example.edu.school.auth.dto.response.RegisterResponse;
import com.example.edu.school.auth.dto.response.UserCredentialResponse;
import com.example.edu.school.auth.model.Role;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImp implements AuthService{
    private final UserClient userClient;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    public RegisterResponse register(RegisterRequest registerRequest) {
        registerRequest.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        if(registerRequest.getRole() == Role.STUDENT ){
            if(((StudentRegisterRequest) registerRequest).getParents() !=null && !((StudentRegisterRequest) registerRequest).getParents().isEmpty()){
                ((StudentRegisterRequest) registerRequest).getParents().forEach(parent -> parent.setPassword(passwordEncoder.encode(parent.getPassword())));
            }
        }
 
        ResponseEntity<BaseResponse<String>> response= userClient.register(registerRequest);
        
            return RegisterResponse.builder()
                    .email(Objects.requireNonNull(response.getBody()).getData())
                    .build();
        
    }

    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword());
        UserCredentialResponse user=(UserCredentialResponse) authenticationManager.authenticate(authenticationToken).getPrincipal();

        return LoginResponse.builder()
                .accessToken(jwtService.generateAccessToken(user.getEmail()))
                .refreshToken(jwtService.generateRefreshToken(user.getEmail()))
                .role(user.getRole())
                .build();
    }

    @Override
    public boolean validateAccessToken(String token) {

        String email = jwtService.extractEmailAccessToken(token);
        ResponseEntity<BaseResponse<UserCredentialResponse>> response = userClient.getUserByEmail(email);
        if(response.getStatusCode()== HttpStatus.NOT_FOUND){
            throw new UsernameNotFoundException("Người dùng không tồn tại: " + email);
        }
        UserCredentialResponse user = Objects.requireNonNull(response.getBody()).getData();
        return  jwtService.validateAccessToken(token, user.getEmail());
    }

    @Override
    public RegisterResponse registerParent(ParentRegisterRequest registerRequest, Long studentId) {
        registerRequest.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        ResponseEntity<BaseResponse<String>> response= userClient.registerParent(registerRequest,studentId);

        return RegisterResponse.builder()
                .email(Objects.requireNonNull(response.getBody()).getData())
                .build();
    }
}
