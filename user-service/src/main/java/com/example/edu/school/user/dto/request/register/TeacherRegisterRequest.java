package com.example.edu.school.user.dto.request.register;


import com.example.edu.school.user.model.Subject;

import jakarta.validation.constraints.NotEmpty;

import lombok.Getter;

import java.util.Set;

@Getter
public class TeacherRegisterRequest extends RegisterRequest {
    @NotEmpty(message = "Môn học không được để trống")
    private Set<Subject> subjects;

}
