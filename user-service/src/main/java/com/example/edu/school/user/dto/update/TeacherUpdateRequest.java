package com.example.edu.school.user.dto.update;

import com.example.edu.school.library.enumeration.Subject;
import lombok.Getter;

import java.util.Set;

@Getter
public class TeacherUpdateRequest extends UserUpdateRequest{

    private Set<Subject> subjects;
}
