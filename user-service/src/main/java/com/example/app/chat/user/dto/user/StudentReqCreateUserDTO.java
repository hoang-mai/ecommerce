package com.example.app.chat.user.dto.user;

import lombok.Getter;

import java.util.Set;

import jakarta.validation.Valid;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class StudentReqCreateUserDTO extends ReqCreateUserDTO {
    
    @Valid
    private Set<ParentReqCreateUserDTO> parents;
}
