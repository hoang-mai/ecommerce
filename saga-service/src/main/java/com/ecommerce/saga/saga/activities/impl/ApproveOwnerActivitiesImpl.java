package com.ecommerce.saga.saga.activities.impl;

import com.ecommerce.auth.AccountServiceGrpc;
import com.ecommerce.auth.ReqUpdateRoleDTO;
import com.ecommerce.library.exception.HttpRequestException;
import com.ecommerce.library.kafka.event.user.UpdateRoleEvent;
import com.ecommerce.library.utils.FnCommon;
import com.ecommerce.library.utils.MessageError;
import com.ecommerce.saga.producer.UserEventProducer;
import com.ecommerce.user.ReqRollbackUpdateUserRoleAndVerificationStatusDTO;
import com.ecommerce.user.ReqUpdateUserRoleAndVerificationStatusDTO;
import com.ecommerce.user.ResUpdateUserRoleAndVerificationStatusDTO;
import com.ecommerce.user.UserServiceGrpc;
import com.ecommerce.saga.saga.activities.ApproveOwnerActivities;
import com.ecommerce.saga.saga.data.ApproveOwnerData;
import com.ecommerce.utils.base_response.BaseResponse;
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
public class ApproveOwnerActivitiesImpl implements ApproveOwnerActivities {

    private final UserServiceGrpc.UserServiceBlockingStub userServiceBlockingStub;
    private final AccountServiceGrpc.AccountServiceBlockingStub accountServiceBlockingStub;
    private final UserEventProducer userEventProducer;

    @Override
    public ApproveOwnerData updateUserRoleAndVerificationStatus(ApproveOwnerData approveOwnerData) {
        try {

            UserServiceGrpc.UserServiceBlockingStub stubWithToken =
                    userServiceBlockingStub.withInterceptors(new BearerTokenAuthenticationInterceptor(approveOwnerData.getToken()));

            BaseResponse baseResponse = stubWithToken.updateUserRoleAndVerificationStatus(
                    ReqUpdateUserRoleAndVerificationStatusDTO.newBuilder()
                            .setUserVerificationId(approveOwnerData.getUserVerificationId())
                            .setNewRole(FnCommon.convertRoleToRoleProto(approveOwnerData.getNewRole()))
                            .setNewVerificationStatus(FnCommon.convertUserVerificationStatusToProto(approveOwnerData.getNewUserVerificationStatus()))
                            .build()
            );

            if (baseResponse.hasData()) {
                try {
                    ResUpdateUserRoleAndVerificationStatusDTO res = baseResponse.getData().unpack(ResUpdateUserRoleAndVerificationStatusDTO.class);
                    approveOwnerData.setOldRole(FnCommon.convertRoleProtoToRole(res.getOldRole()));
                    approveOwnerData.setUserId(res.getUserId());
                    approveOwnerData.setOldUserVerificationStatus(FnCommon.convertUserVerificationStatusProtoToJava(res.getOldVerificationStatus()));
                    return approveOwnerData;
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
            throw new HttpRequestException(
                    e.getStatus().getDescription(),
                    FnCommon.convertGrpcCodeToHttpStatus(e.getStatus().getCode()),
                    timestamp
            );
        }
    }

    @Override
    public void rollbackUserRoleAndVerificationStatus(ApproveOwnerData approveOwnerData) {
        try {
            UserServiceGrpc.UserServiceBlockingStub stubWithToken =
                    userServiceBlockingStub.withInterceptors(new BearerTokenAuthenticationInterceptor(approveOwnerData.getToken()));

            BaseResponse baseResponse = stubWithToken.rollbackUpdateUserRoleAndVerificationStatus(
                    ReqRollbackUpdateUserRoleAndVerificationStatusDTO.newBuilder()
                            .setOldRole(FnCommon.convertRoleToRoleProto(approveOwnerData.getOldRole()))
                            .setOldVerificationStatus(FnCommon.convertUserVerificationStatusToProto(approveOwnerData
                                    .getOldUserVerificationStatus()))
                            .setUserVerificationId(approveOwnerData.getUserVerificationId())
                            .build()
            );

            if (baseResponse.getStatusCode() != HttpStatus.OK.value()) {
                throw new HttpRequestException(
                        MessageError.CANNOT_UPDATE_USER,
                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        LocalDateTime.now()
                );
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
            throw new HttpRequestException(
                    e.getStatus().getDescription(),
                    FnCommon.convertGrpcCodeToHttpStatus(e.getStatus().getCode()),
                    timestamp
            );
        }
    }

    @Override
    public void updateAccountRole(ApproveOwnerData approveOwnerData) {
        try {

            AccountServiceGrpc.AccountServiceBlockingStub stubWithToken =
                    accountServiceBlockingStub.withInterceptors(new BearerTokenAuthenticationInterceptor(approveOwnerData.getToken()));

            BaseResponse baseResponse = stubWithToken.updateRole(
                    ReqUpdateRoleDTO.newBuilder()
                            .setUserId(approveOwnerData.getUserId())
                            .setRole(FnCommon.convertRoleToRoleProto(approveOwnerData.getNewRole()))
                            .build()
            );

            if (baseResponse.getStatusCode() != HttpStatus.OK.value()) {
                throw new HttpRequestException(
                        MessageError.CANNOT_UPDATE_ACCOUNT,
                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        LocalDateTime.now()
                );
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
            throw new HttpRequestException(
                    e.getStatus().getDescription(),
                    FnCommon.convertGrpcCodeToHttpStatus(e.getStatus().getCode()),
                    timestamp
            );
        }
    }

    @Override
    public void rollbackAccountRole(ApproveOwnerData approveOwnerData) {
        try {

            AccountServiceGrpc.AccountServiceBlockingStub stubWithToken =
                    accountServiceBlockingStub.withInterceptors(new BearerTokenAuthenticationInterceptor(approveOwnerData.getToken()));

            BaseResponse baseResponse = stubWithToken.updateRole(
                    ReqUpdateRoleDTO.newBuilder()
                            .setUserId(approveOwnerData.getUserId())
                            .setRole(FnCommon.convertRoleToRoleProto(approveOwnerData.getOldRole()))
                            .build()
            );

            if (baseResponse.getStatusCode() != HttpStatus.OK.value()) {
                throw new HttpRequestException(
                        MessageError.CANNOT_UPDATE_ACCOUNT,
                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        LocalDateTime.now()
                );
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
            throw new HttpRequestException(
                    e.getStatus().getDescription(),
                    FnCommon.convertGrpcCodeToHttpStatus(e.getStatus().getCode()),
                    timestamp
            );
        }
    }

    @Override
    public void updateUserRole(ApproveOwnerData approveOwnerData) {
        userEventProducer.send(UpdateRoleEvent.builder()
                .userId(approveOwnerData.getUserId())
                .role(approveOwnerData.getNewRole())
                .build()
        );
    }
}

