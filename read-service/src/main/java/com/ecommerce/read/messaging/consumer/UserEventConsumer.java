package com.ecommerce.read.messaging.consumer;

import com.ecommerce.library.kafka.event.*;
import com.ecommerce.read.service.UserViewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import static com.ecommerce.library.kafka.Constant.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserEventConsumer {

    private final UserViewService userViewService;

    @KafkaListener(topics = CREATE_USER_TOPIC, groupId = READ_SERVICE_GROUP)
    public void listenCreateUser(CreateUserEvent createUserEvent) {
        userViewService.createUserView(createUserEvent);
    }

    @KafkaListener(topics = UPDATE_USER_TOPIC, groupId = READ_SERVICE_GROUP)
    public void listenUpdateUser(UpdateUserEvent updateUserEvent) {
        userViewService.updateUserView(updateUserEvent);
    }

    @KafkaListener(topics = UPDATE_ROLE_TOPIC, groupId = READ_SERVICE_GROUP)
    public void listenUpdateRole(UpdateRoleEvent updateRoleEvent) {
        userViewService.updateRole(updateRoleEvent);
    }

    @KafkaListener(topics = UPDATE_ACCOUNT_STATUS_TOPIC, groupId = READ_SERVICE_GROUP)
    public void listenUpdateAccountStatus(UpdateAccountStatusEvent updateAccountStatusEvent) {
        userViewService.updateAccountStatus(updateAccountStatusEvent);
    }

    @KafkaListener(topics = UPDATE_AVATAR_URL_TOPIC , groupId = READ_SERVICE_GROUP)
    public void listenUpdateAvatarUrl(UpdateAvatarUserEvent updateAvatarUserEvent) {
        userViewService.updateAvatarUser(updateAvatarUserEvent);
    }
}