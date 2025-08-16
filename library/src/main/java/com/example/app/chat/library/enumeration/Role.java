package com.example.app.chat.library.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Role {
    ADMIN(1L, "ADMIN"),
    USER(2L, "USER");

    private final Long roleId;
    private final String role;
}
