package com.example.edu.school.auth.dto.request.register;

import com.example.edu.school.auth.dto.request.register.ParentRegisterRequest;
import com.example.edu.school.auth.dto.request.register.RegisterRequest;
import jakarta.validation.Valid;
import lombok.Getter;

import java.util.Set;

@Getter
public class StudentRegisterRequest extends RegisterRequest {
    
    @Valid
    private Set<ParentRegisterRequest> parents;
}
