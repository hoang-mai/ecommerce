package com.example.edu.school.auth.saga.activities.impl;

import com.example.edu.school.auth.client.dto.ReqCreateUserDTO;
import com.example.edu.school.auth.dto.account.ReqCreateAccountDTO;
import com.example.edu.school.auth.saga.activities.CreateUserActivities;

public class CreateUserActivitiesImpl implements CreateUserActivities {
    @Override
    public String createAccount(ReqCreateAccountDTO reqCreateAccountDTO) {
        return "";
    }

    @Override
    public void deleteAccount(String accountId) {

    }

    @Override
    public Long createUser(ReqCreateUserDTO reqCreateUserDTO) {
        return 0L;
    }

    @Override
    public void deleteUser(Long userId) {

    }
}
