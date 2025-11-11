package com.ecommerce.saga.saga.activities.impl;

import com.ecommerce.auth.AccountServiceGrpc;
import com.ecommerce.auth.ReqCreateAccountDTO;
import com.ecommerce.auth.ReqDeleteAccountDTO;
import com.ecommerce.auth.ResCreateAccountDTO;
import com.ecommerce.library.exception.HttpRequestException;
import com.ecommerce.library.kafka.event.CreateUserEvent;
import com.ecommerce.library.utils.FnCommon;
import com.ecommerce.library.utils.MessageError;
import com.ecommerce.saga.producer.UserEventProducer;
import com.ecommerce.saga.saga.activities.CreateUserActivities;
import com.ecommerce.saga.saga.data.CreateUserData;
import com.ecommerce.user.ReqCreateUserDTO;
import com.ecommerce.user.ReqDeleteUserDTO;
import com.ecommerce.user.ResCreateUserDTO;
import com.ecommerce.user.UserServiceGrpc;
import com.ecommerce.utils.base_response.BaseResponse;
import com.google.protobuf.InvalidProtocolBufferException;
import io.grpc.Metadata;
import io.grpc.StatusRuntimeException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class CreateUserActivitiesImpl implements CreateUserActivities {

    private final UserServiceGrpc.UserServiceBlockingStub userServiceBlockingStub;
    private final AccountServiceGrpc.AccountServiceBlockingStub accountServiceBlockingStub;
    private final UserEventProducer userEventProducer;

    @Override
    public CreateUserData createUser(CreateUserData createUserData) {
        try {
            BaseResponse baseResponse = userServiceBlockingStub.createUser(
                    ReqCreateUserDTO.newBuilder()
                            .setFirstName(createUserData.getFirstName())
                            .setMiddleName(createUserData.getMiddleName())
                            .setLastName(createUserData.getLastName())
                            .setGender(FnCommon.convertGenderToGenderProto(createUserData.getGender()))
                            .setRole(FnCommon.convertRoleToRoleProto(createUserData.getRole()))
                            .setWard(createUserData.getWard())
                            .setProvince(createUserData.getProvince())
                            .setDetail(createUserData.getDetail())
                            .setReceiverName(createUserData.getReceiverName())
                            .setPhoneNumber(createUserData.getPhoneNumber())
                            .setIsDefault(createUserData.getIsDefault())
                            .build()
            );
            if (baseResponse.hasData()) {
                try {
                    ResCreateUserDTO res = baseResponse.getData().unpack(ResCreateUserDTO.class);
                    createUserData.setUserId(res.getUserId());
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
    public void deleteUser(Long userId) {
        try {
            BaseResponse baseResponse = userServiceBlockingStub.deleteUser(
                    ReqDeleteUserDTO.newBuilder()
                            .setUserId(userId)
                            .build()
            );
            if (baseResponse.getStatusCode() != HttpStatus.OK.value()) {
                throw new HttpRequestException(MessageError.CANNOT_DELETE_USER, HttpStatus.INTERNAL_SERVER_ERROR.value(), LocalDateTime.now());
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
    public void createUserView(CreateUserData createUserData) {
        userEventProducer.send(CreateUserEvent.builder()
                        .userId(createUserData.getUserId())
                        .username(createUserData.getUsername())
                        .firstName(createUserData.getFirstName())
                        .middleName(createUserData.getMiddleName())
                        .lastName(createUserData.getLastName())
                        .accountStatus(createUserData.getAccountStatus())
                        .role(createUserData.getRole())
                .build());
    }


    @Override
    public CreateUserData createAccount(CreateUserData createUserData) {

        try {
            BaseResponse baseResponse = accountServiceBlockingStub.createAccount(ReqCreateAccountDTO.newBuilder()
                    .setUsername(createUserData.getUsername())
                    .setPassword(createUserData.getPassword())
                    .setUserId(createUserData.getUserId())
                    .setRole(FnCommon.convertRoleToRoleProto(createUserData.getRole()))
                    .setAccountStatus(FnCommon.convertAccountStatusToAccountStatusProto(createUserData.getAccountStatus()))
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


}
