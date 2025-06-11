package com.example.edu.school.auth.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Builder
@Getter
public class RegisterResponse {

    private String email;
}
