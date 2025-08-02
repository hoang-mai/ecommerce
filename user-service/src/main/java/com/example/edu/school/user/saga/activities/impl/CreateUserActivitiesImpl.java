package com.example.edu.school.user.saga.activities.impl;

import com.example.edu.school.user.dto.user.ReqCreateUserDTO;
import com.example.edu.school.user.saga.activities.CreateUserActivities;
import com.example.edu.school.user.saga.data.CreateUserData;
import com.example.edu.school.user.saga.service.UserServiceSaga;
import com.example.edu.school.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CreateUserActivitiesImpl implements CreateUserActivities {

    private final UserServiceSaga userServiceSaga;

    @Override
    public String createAccount(CreateUserData createUserData) {
        return "";
    }

    @Override
    public void deleteAccount(String accountId) {

    }

    @Override
    public Long createUser(CreateUserData createUserData) {
        return userServiceSaga.saveUser(createUserData);
    }

    @Override
    public void deleteUser(Long userId) {
        userServiceSaga.deleteUser(userId);
    }
}
