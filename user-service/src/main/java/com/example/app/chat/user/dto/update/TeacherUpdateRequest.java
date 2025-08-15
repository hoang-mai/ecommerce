package com.example.app.chat.user.dto.update;

import com.example.app.chat.library.enumeration.Subject;
import lombok.Getter;

import java.util.Set;

@Getter
public class TeacherUpdateRequest extends UserUpdateRequest{

    private Set<Subject> subjects;
}
