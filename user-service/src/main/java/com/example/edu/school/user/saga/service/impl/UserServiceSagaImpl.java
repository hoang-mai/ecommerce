package com.example.edu.school.user.saga.service.impl;

import com.example.edu.school.user.saga.data.CreateUserData;
import com.example.edu.school.user.saga.service.UserServiceSaga;
import com.example.edu.school.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserServiceSagaImpl implements UserServiceSaga {

    private final UserService userService;

    @Override
    public void saveUser(CreateUserData createUserData) {
        userService.saveUser(createUserData);
    }

    @Override
    public void deleteUser(Long userId) {
        userService.deleteUser(userId);
    }
}
