package com.example.edu.school.user.saga.activities.impl;

import com.example.edu.school.auth.AccountServiceGrpc;
import com.example.edu.school.auth.ReqCreateAccountDTO;
import com.example.edu.school.auth.ResCreateAccountDTO;
import com.example.edu.school.library.enumeration.Role;
import com.example.edu.school.user.saga.activities.CreateUserActivities;
import com.example.edu.school.user.saga.data.CreateUserData;
import com.example.edu.school.user.saga.service.UserServiceSaga;
import com.example.edu.school.utils.base_response.BaseResponse;
import com.google.protobuf.InvalidProtocolBufferException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CreateUserActivitiesImpl implements CreateUserActivities {

    private final UserServiceSaga userServiceSaga;
    private final AccountServiceGrpc.AccountServiceBlockingStub accountServiceBlockingStub;

    @Override
    public String createAccount(CreateUserData createUserData) {
        BaseResponse baseResponse = accountServiceBlockingStub.createAccount(ReqCreateAccountDTO.newBuilder()
                .setEmail(createUserData.getEmail())
                .setPassword(createUserData.getPassword())
                .setUserId(createUserData.getUserId())
                .setRole(convertRoleToProtoRole(createUserData.getRole()))
                .build());
        if (baseResponse.hasData()) {
            try {
                ResCreateAccountDTO res = baseResponse.getData().unpack(ResCreateAccountDTO.class);
                return res.getAccountId();
            } catch (InvalidProtocolBufferException e) {
                throw new RuntimeException(e);
            }
        }else {
            throw new RuntimeException("Failed to create account: " + baseResponse.getMessage());
        }
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

    private com.example.edu.school.enumeration.Role convertRoleToProtoRole(Role role) {
        return switch (role) {
            case ADMIN -> com.example.edu.school.enumeration.Role.ADMIN;
            case PRINCIPAL -> com.example.edu.school.enumeration.Role.PRINCIPAL;
            case ASSISTANT -> com.example.edu.school.enumeration.Role.ASSISTANT;
            case TEACHER -> com.example.edu.school.enumeration.Role.TEACHER;
            case STUDENT -> com.example.edu.school.enumeration.Role.STUDENT;
            case PARENT -> com.example.edu.school.enumeration.Role.PARENT;
        };
    }
}
