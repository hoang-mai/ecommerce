package com.example.app.chat.user.dto.user;


import com.example.app.chat.library.enumeration.Subject;

import jakarta.validation.constraints.NotEmpty;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.util.Set;

@Getter
@SuperBuilder
public class TeacherReqCreateUserDTO extends ReqCreateUserDTO {
    @NotEmpty(message = "Môn học không được để trống")
    private Set<Subject> subjects;

}
