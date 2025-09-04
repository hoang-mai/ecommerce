package com.example.app.chat.chat.service;

import com.example.app.chat.chat.dto.ReqUpdateChatMemberDTO;

public interface ChatMemberService {

    void updateChatMember(String chatMemberId, ReqUpdateChatMemberDTO reqUpdateChatMemberDTO);
}
