package com.example.edu.school.chat.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum MessageType {
    TEXT("text"),
    IMAGE("image"),
    VIDEO("video"),
    AUDIO("audio"),
    FILE("file"),
    LOCATION("location"),
    STICKER("sticker"),
    GIF("gif"),
    POLL("poll"),
    REACTION("reaction"),
    LINK("link"),
    OTHER("other");

    private final String type;

}
