package com.example.edu.school.auth.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {
    STUDENT(1,"student"),
    TEACHER(2,"teacher"),
    PARENT(3,"parent"),
    ASSISTANT(4,"assistant"),
    ADMIN(5,"admin");

    private final int id;
    private final String name;
}
