package com.example.app.chat.user.dto.information;

import java.util.Set;

import com.example.app.chat.library.enumeration.Subject;
import lombok.Getter;
import lombok.experimental.SuperBuilder;


@SuperBuilder
@Getter
public class TeacherResponse extends UserResponse{

    private Set<Subject> subjects;
}
