package com.example.edu.school.auth.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Subject {

    MATH(1,"math"),
    PHYSICS(2,"physics"),
    CHEMISTRY(3,"chemistry"),
    BIOLOGY(4,"biology"),
    ENGLISH(5,"english"),
    HISTORY(6,"history"),
    GEOGRAPHY(7,"geography"),
    LITERATURE (8,"literature"),
    ART(9,"art"),
    MUSIC(10,"music"),
    CIVIC_EDUCATION(11,"civic education"),
    PHYSICAL_EDUCATION(12,"physical education"),
    INFORMATICS(13,"informatics"),
    TECHNOLOGY (14,"technology"),
    OTHER(15,"other");

    private final int id;
    private final String subjectName;

}