package com.ecommerce.chat.service.impl;

import com.ecommerce.chat.dto.ReqMarkAsReadDTO;
import com.ecommerce.chat.repository.UserChatRepository;
import com.ecommerce.chat.service.UserChatService;
import com.ecommerce.library.component.UserHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserChatServiceImpl implements UserChatService {

    private final UserChatRepository userChatRepository;
    private final UserHelper userHelper;

    @Transactional
    @Override
    public void markChatAsRead(ReqMarkAsReadDTO reqMarkAsReadDTO) {
        userChatRepository.findById(reqMarkAsReadDTO.getUserChatId())
                .ifPresent(userChat -> {
                    userChat.setIsRead(true);
                    userChatRepository.save(userChat);
                });
    }

    @Override
    public long getUnreadCount() {
        Long currentUserId = userHelper.getCurrentUserId();
        return userChatRepository.countByUserIdAndIsReadFalse(String.valueOf(currentUserId));
    }

}

