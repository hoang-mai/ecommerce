package com.example.app.chat.chat.dto;

import com.example.app.chat.chat.entity.User;
import lombok.Getter;

import java.util.Set;

@Getter
public class ReqCreateGroupChatDTO {
    private Set<Long> userIds;
}
