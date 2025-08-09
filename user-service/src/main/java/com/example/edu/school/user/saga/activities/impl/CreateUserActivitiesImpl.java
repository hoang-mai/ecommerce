package com.example.edu.school.user.saga.activities.impl;

import com.example.edu.school.auth.AccountServiceGrpc;
import com.example.edu.school.auth.ReqCreateAccountDTO;
import com.example.edu.school.auth.ReqDeleteAccountDTO;
import com.example.edu.school.auth.ResCreateAccountDTO;
import com.example.edu.school.library.enumeration.Role;
import com.example.edu.school.library.exception.HttpRequestException;
import com.example.edu.school.library.utils.FnCommon;
import com.example.edu.school.library.utils.MessageError;
import com.example.edu.school.user.saga.activities.CreateUserActivities;
import com.example.edu.school.user.saga.data.CreateUserData;
import com.example.edu.school.user.saga.service.UserServiceSaga;
import com.example.edu.school.utils.base_response.BaseResponse;
import com.google.protobuf.InvalidProtocolBufferException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

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
                .setRole(FnCommon.convertRoleToRoleProto(createUserData.getRole()))
                .build());
        if (baseResponse.hasData()) {
            try {
                ResCreateAccountDTO res = baseResponse.getData().unpack(ResCreateAccountDTO.class);
                return res.getAccountId();
            } catch (InvalidProtocolBufferException e) {
                throw new HttpRequestException(MessageError.CANNOT_READ_RESPONSE_FROM_SERVER, HttpStatus.INTERNAL_SERVER_ERROR.value(), LocalDateTime.now());
            }
        } else {
            throw new HttpRequestException(MessageError.CANNOT_READ_RESPONSE_FROM_SERVER, HttpStatus.INTERNAL_SERVER_ERROR.value(), LocalDateTime.now());
        }
    }

    @Override
    public void deleteAccount(String accountId) {
        BaseResponse baseResponse = accountServiceBlockingStub.deleteAccount(ReqDeleteAccountDTO.newBuilder().setAccountId(accountId).build());
        if (baseResponse.getStatusCode()!=HttpStatus.OK.value()) {
            throw new HttpRequestException(MessageError.CANNOT_DELETE_ACCOUNT, HttpStatus.INTERNAL_SERVER_ERROR.value(), LocalDateTime.now());
        }
    }

    @Override
    public CreateUserData createUser(CreateUserData createUserData) {
        return userServiceSaga.saveUser(createUserData);
    }

    @Override
    public void deleteUser(Long userId) {
        userServiceSaga.deleteUser(userId);
    }

}
