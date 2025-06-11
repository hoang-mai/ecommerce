package com.example.edu.school.auth.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ParentRelationship {

    FATHER(1, "father"),
    MOTHER(2, "mother"),
    PATERNAL_GRANDFATHER(3, "paternal grandfather"),
    PATERNAL_GRANDMOTHER(4, "paternal grandmother"),
    MATERNAL_GRANDFATHER(5, "maternal grandfather"),
    MATERNAL_GRANDMOTHER(6, "maternal grandmother"),
    UNCLE(7, "uncle"),
    AUNT(8, "aunt"),
    BROTHER(9, "brother"),
    SISTER(10, "sister"),
    OTHER(11, "other");

    private final int id;
    private final String name;

}