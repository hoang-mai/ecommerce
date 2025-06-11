package com.example.edu.school.user.dto.response;


import com.example.edu.school.user.model.Role;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Builder
@Setter
public class UserCredentialResponse {
    private String email;
    private String password;
    private Role role;
}
