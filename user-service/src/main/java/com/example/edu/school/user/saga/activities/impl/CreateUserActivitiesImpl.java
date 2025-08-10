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
import io.grpc.Metadata;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.MetadataUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.grpc.client.interceptor.security.BearerTokenAuthenticationInterceptor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class CreateUserActivitiesImpl implements CreateUserActivities {

    private final UserServiceSaga userServiceSaga;
    private final AccountServiceGrpc.AccountServiceBlockingStub accountServiceBlockingStub;

    @Override
    public CreateUserData createAccount(CreateUserData createUserData) {

        AccountServiceGrpc.AccountServiceBlockingStub stubWithToken = accountServiceBlockingStub.withInterceptors(new BearerTokenAuthenticationInterceptor(createUserData.getToken()));
        try {
            BaseResponse baseResponse = stubWithToken.createAccount(ReqCreateAccountDTO.newBuilder()
                    .setEmail(createUserData.getEmail())
                    .setPassword(createUserData.getPassword())
                    .setUserId(createUserData.getUserId())
                    .setRole(FnCommon.convertRoleToRoleProto(createUserData.getRole()))
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
        AccountServiceGrpc.AccountServiceBlockingStub stubWithToken = accountServiceBlockingStub.withInterceptors(new BearerTokenAuthenticationInterceptor(createUserData.getToken()));
        try {
            BaseResponse baseResponse = stubWithToken.deleteAccount(ReqDeleteAccountDTO.newBuilder().setAccountId(createUserData.getAccountId()).build());
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
