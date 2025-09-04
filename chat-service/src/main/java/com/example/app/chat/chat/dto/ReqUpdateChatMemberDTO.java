package com.example.app.chat.chat.dto;

import lombok.Getter;

@Getter
public class ReqUpdateChatMemberDTO {
    private String nickname;
    private Boolean isAdmin;
    private Boolean isDeleted;
}
