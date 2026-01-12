package com.ecommerce.saga.saga.activities.impl;

import com.ecommerce.auth.AccountServiceGrpc;
import com.ecommerce.auth.ReqCreateAccountDTO;
import com.ecommerce.auth.ReqDeleteAccountDTO;
import com.ecommerce.auth.ResCreateAccountDTO;
import com.ecommerce.enumeration.Gender;
import com.ecommerce.library.exception.HttpRequestException;
import com.ecommerce.library.kafka.event.user.CreateUserEvent;
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

import java.time.Instant;

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
                            .setFullName(createUserData.getFullName())
                            .setGender(FnCommon.isNotNull(createUserData.getGender()) ? FnCommon.convertGenderToGenderProto(createUserData.getGender()) : Gender.GENDER_UNSPECIFIED)
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
                    createUserData.setCreatedAt(FnCommon.convertTimestampToInstant(res.getCreatedAt()));
                    createUserData.setUpdatedAt(FnCommon.convertTimestampToInstant(res.getUpdatedAt()));
                    return createUserData;
                } catch (InvalidProtocolBufferException e) {
                    throw new HttpRequestException(MessageError.CANNOT_READ_RESPONSE_FROM_SERVER, HttpStatus.INTERNAL_SERVER_ERROR.value(), Instant.now());
                }
            } else {
                throw new HttpRequestException(MessageError.CANNOT_READ_RESPONSE_FROM_SERVER, HttpStatus.INTERNAL_SERVER_ERROR.value(), Instant.now());
            }
        } catch (StatusRuntimeException e) {
            Metadata metadata = e.getTrailers();
            Instant timestamp = Instant.now();
            if (metadata != null && metadata.containsKey(Metadata.Key.of("timestamp", Metadata.ASCII_STRING_MARSHALLER))) {
                String timeStamp = metadata.get(Metadata.Key.of("timestamp", Metadata.ASCII_STRING_MARSHALLER));
                if (timeStamp != null && !timeStamp.isEmpty()) {
                    try {
                        timestamp = Instant.parse(timeStamp);
                    } catch (Exception ex) {
                        // ignore parse error and keep now
                    }
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
                throw new HttpRequestException(MessageError.CANNOT_DELETE_USER, HttpStatus.INTERNAL_SERVER_ERROR.value(), Instant.now());
            }
        } catch (StatusRuntimeException e) {
            Metadata metadata = e.getTrailers();
            Instant timestamp = Instant.now();
            if (metadata != null && metadata.containsKey(Metadata.Key.of("timestamp", Metadata.ASCII_STRING_MARSHALLER))) {
                String timeStamp = metadata.get(Metadata.Key.of("timestamp", Metadata.ASCII_STRING_MARSHALLER));
                if (timeStamp != null && !timeStamp.isEmpty()) {
                    try {
                        timestamp = Instant.parse(timeStamp);
                    } catch (Exception ex) {
                        // ignore parse error
                    }
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
                .fullName(createUserData.getFullName())
                .accountStatus(createUserData.getAccountStatus())
                .role(createUserData.getRole())
                .createdAt(createUserData.getCreatedAt())
                .updatedAt(createUserData.getUpdatedAt())
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
                    throw new HttpRequestException(MessageError.CANNOT_READ_RESPONSE_FROM_SERVER, HttpStatus.INTERNAL_SERVER_ERROR.value(), Instant.now());
                }
            } else {
                throw new HttpRequestException(MessageError.CANNOT_READ_RESPONSE_FROM_SERVER, HttpStatus.INTERNAL_SERVER_ERROR.value(), Instant.now());
            }
        } catch (StatusRuntimeException e) {
            Metadata metadata = e.getTrailers();
            Instant timestamp = Instant.now();
            if (metadata != null && metadata.containsKey(Metadata.Key.of("timestamp", Metadata.ASCII_STRING_MARSHALLER))) {
                String timeStamp = metadata.get(Metadata.Key.of("timestamp", Metadata.ASCII_STRING_MARSHALLER));
                if (timeStamp != null && !timeStamp.isEmpty()) {
                    try {
                        timestamp = Instant.parse(timeStamp);
                    } catch (Exception ex) {
                        // ignore parse error
                    }
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
                throw new HttpRequestException(MessageError.CANNOT_DELETE_ACCOUNT, HttpStatus.INTERNAL_SERVER_ERROR.value(), Instant.now());
            }
        } catch (StatusRuntimeException e) {
            Metadata metadata = e.getTrailers();
            Instant timestamp = Instant.now();
            if (metadata != null && metadata.containsKey(Metadata.Key.of("timestamp", Metadata.ASCII_STRING_MARSHALLER))) {
                String timeStamp = metadata.get(Metadata.Key.of("timestamp", Metadata.ASCII_STRING_MARSHALLER));
                if (timeStamp != null && !timeStamp.isEmpty()) {
                    try {
                        timestamp = Instant.parse(timeStamp);
                    } catch (Exception ex) {
                        // ignore parse error
                    }
                }
            }
            throw new HttpRequestException(e.getStatus().getDescription(), FnCommon.convertGrpcCodeToHttpStatus(e.getStatus().getCode()), timestamp);
        }
    }


}
