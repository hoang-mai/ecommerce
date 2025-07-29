package com.example.edu.school.user.dto.user;

import lombok.Getter;

import java.util.Set;

import jakarta.validation.Valid;

@Getter
public class StudentReqCreateUserDTO extends ReqCreateUserDTO {
    
    @Valid
    private Set<ParentReqCreateUserDTO> parents;
}
