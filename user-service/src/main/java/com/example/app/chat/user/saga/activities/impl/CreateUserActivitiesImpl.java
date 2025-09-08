package com.example.app.chat.user.saga.activities.impl;

import com.example.app.chat.auth.AccountServiceGrpc;
import com.example.app.chat.auth.ReqCreateAccountDTO;
import com.example.app.chat.auth.ReqDeleteAccountDTO;
import com.example.app.chat.auth.ResCreateAccountDTO;
import com.example.app.chat.library.enumeration.Role;
import com.example.app.chat.library.exception.HttpRequestException;
import com.example.app.chat.library.utils.FnCommon;
import com.example.app.chat.library.utils.MessageError;
import com.example.app.chat.user.saga.activities.CreateUserActivities;
import com.example.app.chat.user.saga.data.CreateUserData;
import com.example.app.chat.user.saga.service.UserServiceSaga;
import com.example.app.chat.utils.base_response.BaseResponse;
import com.google.protobuf.InvalidProtocolBufferException;
import io.grpc.Metadata;
import io.grpc.StatusRuntimeException;
import lombok.RequiredArgsConstructor;
import org.springframework.grpc.client.interceptor.security.BearerTokenAuthenticationInterceptor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class CreateUserActivitiesImpl implements CreateUserActivities {

    private final UserServiceSaga userServiceSaga;
    private final AccountServiceGrpc.AccountServiceBlockingStub accountServiceBlockingStub;

    @Override
    public CreateUserData createAccount(CreateUserData createUserData) {

        try {
            BaseResponse baseResponse = accountServiceBlockingStub.createAccount(ReqCreateAccountDTO.newBuilder()
                    .setUsername(createUserData.getUsername())
                    .setPassword(createUserData.getPassword())
                    .setUserId(createUserData.getUserId())
                    .setRole(FnCommon.convertRoleToRoleProto(Role.USER))
                    .build());
            if (baseResponse.hasData()) {
                try {
                    ResCreateAccountDTO res = baseResponse.getData().unpack(ResCreateAccountDTO.class);
                    createUserData.setAccountId(res.getAccountId());
                    return createUserData;
                } catch (InvalidProtocolBufferException e) {
                    throw new HttpRequestException(MessageError.CANNOT_READ_RESPONSE_FROM_SERVER, HttpStatus.INTERNAL_SERVER_ERROR.value(), LocalDateTime.now());
                }
            } else {
                throw new HttpRequestException(MessageError.CANNOT_READ_RESPONSE_FROM_SERVER, HttpStatus.INTERNAL_SERVER_ERROR.value(), LocalDateTime.now());
            }
        } catch (StatusRuntimeException e) {
            Metadata metadata = e.getTrailers();
            LocalDateTime timestamp = LocalDateTime.now();
            if (metadata != null && metadata.containsKey(Metadata.Key.of("timestamp", Metadata.ASCII_STRING_MARSHALLER))) {
                String timeStamp = metadata.get(Metadata.Key.of("timestamp", Metadata.ASCII_STRING_MARSHALLER));
                if (timeStamp != null && !timeStamp.isEmpty()) {
                    timestamp = LocalDateTime.parse(timeStamp);
                }
            }
            throw new HttpRequestException(e.getStatus().getDescription(), FnCommon.convertGrpcCodeToHttpStatus(e.getStatus().getCode()), timestamp);
        }
    }

    @Override
    public void deleteAccount(CreateUserData createUserData) {
        try {
            BaseResponse baseResponse = accountServiceBlockingStub.deleteAccount(ReqDeleteAccountDTO.newBuilder().setAccountId(createUserData.getAccountId()).build());
            if (baseResponse.getStatusCode() != HttpStatus.OK.value()) {
                throw new HttpRequestException(MessageError.CANNOT_DELETE_ACCOUNT, HttpStatus.INTERNAL_SERVER_ERROR.value(), LocalDateTime.now());
            }
        } catch (StatusRuntimeException e) {
            Metadata metadata = e.getTrailers();
            LocalDateTime timestamp = LocalDateTime.now();
            if (metadata != null && metadata.containsKey(Metadata.Key.of("timestamp", Metadata.ASCII_STRING_MARSHALLER))) {
                String timeStamp = metadata.get(Metadata.Key.of("timestamp", Metadata.ASCII_STRING_MARSHALLER));
                if (timeStamp != null && !timeStamp.isEmpty()) {
                    timestamp = LocalDateTime.parse(timeStamp);
                }
            }
            throw new HttpRequestException(e.getStatus().getDescription(), FnCommon.convertGrpcCodeToHttpStatus(e.getStatus().getCode()), timestamp);
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
