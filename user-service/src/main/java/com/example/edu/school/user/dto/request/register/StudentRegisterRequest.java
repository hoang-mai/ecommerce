package com.example.edu.school.user.dto.request.register;

import lombok.Getter;

import java.util.Set;

import jakarta.validation.Valid;

@Getter
public class StudentRegisterRequest extends RegisterRequest {
    
    @Valid
    private Set<ParentRegisterRequest> parents;
}
