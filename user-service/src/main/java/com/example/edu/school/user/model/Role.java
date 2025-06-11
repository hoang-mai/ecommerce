package com.example.edu.school.user.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {
    STUDENT(1,"ST","student"),
    TEACHER(2,"TE","teacher"),
    PARENT(3,"PA","parent"),
    ASSISTANT(4,"AS","assistant"),
    ADMIN(5,"AD","admin");

    private final int id;
    private final String code;
    private final String name;

}
