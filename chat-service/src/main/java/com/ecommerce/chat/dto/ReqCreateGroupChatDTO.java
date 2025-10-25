package com.ecommerce.chat.dto;

import lombok.Getter;

import java.util.Set;

@Getter
public class ReqCreateGroupChatDTO {
    private Set<Long> userIds;
}
