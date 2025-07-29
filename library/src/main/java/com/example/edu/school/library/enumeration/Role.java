package com.example.edu.school.library.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Role {
    ADMIN(1L, "ADMIN"),
    PRINCIPAL(2L, "PRINCIPAL"),
    ASSISTANT(3L, "ASSISTANT"),
    TEACHER(4L, "TEACHER"),
    STUDENT(5L, "STUDENT"),
    PARENT(6L, "PARENT");

    private final Long roleId;
    private final String role;
}
