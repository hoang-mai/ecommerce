package com.example.app.chat.library.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Gender {

    MALE(1L,"MALE"),
    FEMALE(2L,"FEMALE"),
    OTHER(3L,"OTHER");

    private final Long genderId;
    private final String genderName;
    
}
