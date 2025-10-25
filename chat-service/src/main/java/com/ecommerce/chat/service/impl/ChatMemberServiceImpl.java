package com.ecommerce.chat.service.impl;

import com.ecommerce.chat.dto.ReqUpdateChatMemberDTO;
import com.ecommerce.chat.entity.ChatMember;
import com.ecommerce.chat.repository.ChatMemberRepository;
import com.ecommerce.chat.service.ChatMemberService;
import com.ecommerce.library.component.UserHelper;
import com.ecommerce.library.utils.FnCommon;
import com.ecommerce.library.utils.MessageError;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatMemberServiceImpl implements ChatMemberService {
    private final ChatMemberRepository chatMemberRepository;
    private final UserHelper userHelper;

    @Override
    public void updateChatMember(String chatMemberId, ReqUpdateChatMemberDTO reqUpdateChatMemberDTO) {
        ChatMember chatMember = chatMemberRepository.findById(chatMemberId)
                .orElseThrow(() -> new RuntimeException(MessageError.CHAT_MEMBER_NOT_FOUND));
        if (FnCommon.isNotNullOrEmpty(reqUpdateChatMemberDTO.getNickname())) {
            chatMember.setNickName(reqUpdateChatMemberDTO.getNickname());
        }
        if (FnCommon.isNotNull(reqUpdateChatMemberDTO.getIsDeleted()) && reqUpdateChatMemberDTO.getIsDeleted()) {
            Long userId = userHelper.getCurrentUserId();
            if (!userId.equals(chatMember.getUserId())) {
                ChatMember currentChatMember = chatMemberRepository.findByChatIdAndUserId(chatMember.getChatId(), userId)
                        .orElseThrow(() -> new RuntimeException(MessageError.CHAT_MEMBER_NOT_FOUND));
                if (!currentChatMember.getIsAdmin()) {
                    throw new RuntimeException(MessageError.FORBIDDEN);
                }
            }
            chatMember.setIsDeleted(true);
        }
        if (FnCommon.isNotNull(reqUpdateChatMemberDTO.getIsAdmin())) {
            Long userId = userHelper.getCurrentUserId();
            ChatMember currentChatMember = chatMemberRepository.findByChatIdAndUserId(chatMember.getChatId(), userId)
                    .orElseThrow(() -> new RuntimeException(MessageError.CHAT_MEMBER_NOT_FOUND));
            if (!currentChatMember.getIsAdmin()) {
                throw new RuntimeException(MessageError.FORBIDDEN);
            }
            chatMember.setIsAdmin(reqUpdateChatMemberDTO.getIsAdmin());
        }
        chatMemberRepository.save(chatMember);
    }
}
