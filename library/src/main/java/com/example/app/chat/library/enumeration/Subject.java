package com.example.app.chat.library.enumeration;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Subject {

    MATH(1L, "MATH"),
    PHYSICS(2L, "PHYSICS"),
    CHEMISTRY(3L, "CHEMISTRY"),
    BIOLOGY(4L, "BIOLOGY"),
    ENGLISH(5L, "ENGLISH"),
    HISTORY(6L, "HISTORY"),
    GEOGRAPHY(7L, "GEOGRAPHY"),
    LITERATURE(8L, "LITERATURE"),
    ART(9L, "ART"),
    MUSIC(10L, "MUSIC"),
    CIVIC_EDUCATION(11L, "CIVIC_EDUCATION"),
    PHYSICAL_EDUCATION(12L, "PHYSICAL_EDUCATION"),
    INFORMATICS(13L, "INFORMATICS"),
    TECHNOLOGY(14L, "TECHNOLOGY"),
    OTHER(15L, "OTHER");

    private final Long subjectId;
    private final String subjectName;

}