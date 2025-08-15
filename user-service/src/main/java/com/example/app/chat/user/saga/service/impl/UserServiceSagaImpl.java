package com.example.app.chat.user.saga.service.impl;

import com.example.app.chat.user.saga.data.CreateUserData;
import com.example.app.chat.user.saga.service.UserServiceSaga;
import com.example.app.chat.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserServiceSagaImpl implements UserServiceSaga {

    private final UserService userService;

    @Override
    public CreateUserData saveUser(CreateUserData createUserData) {
        return userService.saveUser(createUserData);
    }

    @Override
    public void deleteUser(Long userId) {
        userService.deleteUser(userId);
    }
}
