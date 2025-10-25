package com.ecommerce.chat.service;

import com.ecommerce.chat.dto.ReqUpdateChatMemberDTO;

public interface ChatMemberService {

    void updateChatMember(String chatMemberId, ReqUpdateChatMemberDTO reqUpdateChatMemberDTO);
}
