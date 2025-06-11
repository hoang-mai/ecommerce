package com.example.edu.school.auth.dto.response;

import com.example.edu.school.auth.model.Role;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Builder
@Getter
public class LoginResponse {
    private String accessToken;
    private String refreshToken;
    private Role role;
}
