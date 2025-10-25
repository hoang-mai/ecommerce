package com.ecommerce.library.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Role {
    ADMIN(1L, "ADMIN"),
    OWNER(2L, "OWNER"),
    CASHIER(3L, "CASHIER"),
    USER(4L, "USER");

    private final Long roleId;
    private final String role;
}
