package com.example.edu.school.user.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Gender {

    MALE(1,"male"),
    FEMALE(2,"female"),
    OTHER(3,"other");

    private final int id;
    private final String name;
    

}
