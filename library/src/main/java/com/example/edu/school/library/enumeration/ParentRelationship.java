package com.example.edu.school.library.enumeration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ParentRelationship {

    FATHER(1L, "FATHER"),
    MOTHER(2L, "MOTHER"),
    PATERNAL_GRANDFATHER(3L, "PATERNAL_GRANDFATHER"),
    PATERNAL_GRANDMOTHER(4L, "PATERNAL_GRANDMOTHER"),
    MATERNAL_GRANDFATHER(5L, "MATERNAL_GRANDFATHER"),
    MATERNAL_GRANDMOTHER(6L, "MATERNAL_GRANDMOTHER"),
    UNCLE(7L, "UNCLE"),
    AUNT(8L, "AUNT"),
    BROTHER(9L, "BROTHER"),
    SISTER(10L, "SISTER"),
    OTHER(11L, "OTHER");

    private final Long id;
    private final String parentRelationshipName;

}
