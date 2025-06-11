package com.example.edu.school.user.dto.request.update;

import com.example.edu.school.user.model.Subject;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.util.Set;

@Getter
public class TeacherUpdateRequest extends UserUpdateRequest{

    private Set<Subject> subjects;
}
