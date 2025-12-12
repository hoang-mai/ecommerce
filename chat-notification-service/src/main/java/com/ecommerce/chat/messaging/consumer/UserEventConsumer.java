package com.ecommerce.chat.messaging.consumer;

import com.ecommerce.chat.service.ChatService;
import com.ecommerce.chat.service.UserCacheService;
import com.ecommerce.library.kafka.event.user.CreateUserEvent;
import com.ecommerce.library.kafka.event.user.UpdateAccountStatusEvent;
import com.ecommerce.library.kafka.event.user.UpdateAvatarUserEvent;
import com.ecommerce.library.kafka.event.user.UpdateUserEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import static com.ecommerce.library.kafka.Constant.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserEventConsumer {

    private final UserCacheService userCacheService;
    private final ChatService chatService;

    @KafkaListener(topics = CREATE_USER_TOPIC, groupId = CHAT_SERVICE_GROUP)
    public void listen(CreateUserEvent createUserEvent) {
            userCacheService.createUserCache(createUserEvent);
    }

    @KafkaListener(topics = UPDATE_USER_TOPIC, groupId = CHAT_SERVICE_GROUP)
    public void listen(UpdateUserEvent updateUserEvent) {
            userCacheService.updateUserCache(updateUserEvent);
            chatService.updateUserInChats(updateUserEvent);
    }

    @KafkaListener(topics = UPDATE_AVATAR_URL_TOPIC, groupId = CHAT_SERVICE_GROUP)
    public void listen(UpdateAvatarUserEvent updateAvatarUserEvent) {
            userCacheService.updateAvatarUrl(updateAvatarUserEvent.getUserId(), updateAvatarUserEvent.getAvatarUrl());
            chatService.updateAvatarInChats(updateAvatarUserEvent.getUserId(), updateAvatarUserEvent.getAvatarUrl());
    }

    @KafkaListener(topics = UPDATE_ACCOUNT_STATUS_TOPIC , groupId = CHAT_SERVICE_GROUP)
    public void listen(UpdateAccountStatusEvent updateAccountStatusEvent) {
        userCacheService.updateAccountStatus(updateAccountStatusEvent.getUserId(), updateAccountStatusEvent.getAccountStatus());
    }
}

