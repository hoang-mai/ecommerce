package com.example.edu.school.user.dto.user;


import com.example.edu.school.library.enumeration.Subject;

import jakarta.validation.constraints.NotEmpty;

import lombok.Getter;

import java.util.Set;

@Getter
public class TeacherReqCreateUserDTO extends ReqCreateUserDTO {
    @NotEmpty(message = "Môn học không được để trống")
    private Set<Subject> subjects;

}
