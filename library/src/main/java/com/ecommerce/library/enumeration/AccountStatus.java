package com.ecommerce.library.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum AccountStatus {

    ACTIVE(1L,"ACTIVE"),
    INACTIVE(2L, "INACTIVE"),
    SUSPENDED(3L, "SUSPENDED");

    private final Long statusId;

    private final String status;
}
