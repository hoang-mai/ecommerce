package com.example.app.chat.library.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum AccountStatus {

    ACTIVE(1L,"ACTIVE"),
    BAN(2L, "BAN"),
    GRADUATED(3L,"GRADUATED"),;

    private final Long statusId;

    private final String status;
}
