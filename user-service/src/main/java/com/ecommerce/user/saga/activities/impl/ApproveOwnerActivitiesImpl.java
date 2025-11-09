package com.ecommerce.user.saga.activities.impl;

import com.ecommerce.auth.AccountServiceGrpc;
import com.ecommerce.auth.ReqUpdateRoleDTO;
import com.ecommerce.library.component.UserHelper;
import com.ecommerce.library.enumeration.Role;
import com.ecommerce.library.exception.HttpRequestException;
import com.ecommerce.library.exception.NotFoundException;
import com.ecommerce.library.utils.FnCommon;
import com.ecommerce.library.utils.MessageError;
import com.ecommerce.user.entity.User;
import com.ecommerce.user.entity.UserVerification;
import com.ecommerce.user.enumeration.UserVerificationStatus;
import com.ecommerce.user.repository.UserRepository;
import com.ecommerce.user.repository.UserVerificationRepository;
import com.ecommerce.user.saga.activities.ApproveOwnerActivities;
import com.ecommerce.user.saga.data.ApproveOwnerData;
import com.ecommerce.user.saga.service.UserServiceSaga;
import com.ecommerce.user.saga.service.UserVerificationServiceSaga;
import com.ecommerce.utils.base_response.BaseResponse;
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
    private final UserServiceSaga userServiceSaga;
    private final UserVerificationServiceSaga userVerificationServiceSaga;
    private final AccountServiceGrpc.AccountServiceBlockingStub accountServiceBlockingStub;
    private final UserHelper userHelper;

    @Override
    public ApproveOwnerData updateUserRole(ApproveOwnerData approveOwnerData) {
        Role oldRole = userServiceSaga.updateUserRole(approveOwnerData);
        approveOwnerData.setOldRole(oldRole);
        return approveOwnerData;
    }

    @Override
    public void rollbackUserRole(ApproveOwnerData approveOwnerData) {
        userServiceSaga.rollbackUserRole(approveOwnerData);
    }

    @Override
    public void updateAccountRole(ApproveOwnerData approveOwnerData) {
        try {


            AccountServiceGrpc.AccountServiceBlockingStub stubWithToken =
                    accountServiceBlockingStub.withInterceptors(new BearerTokenAuthenticationInterceptor(approveOwnerData.getToken()));

            BaseResponse baseResponse = stubWithToken.updateRole(
                    ReqUpdateRoleDTO.newBuilder()
                            .setRole(FnCommon.convertRoleToRoleProto(Role.OWNER))
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
            BaseResponse baseResponse = accountServiceBlockingStub.updateRole(
                    ReqUpdateRoleDTO.newBuilder()
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
    public void updateVerificationStatus(ApproveOwnerData approveOwnerData) {
        userVerificationServiceSaga.updateUserVerificationStatus(
                approveOwnerData.getUserVerificationId(),
                UserVerificationStatus.APPROVED
        );
    }

    @Override
    public void rollbackVerificationStatus(ApproveOwnerData approveOwnerData) {
        userVerificationServiceSaga.updateUserVerificationStatus(
                approveOwnerData.getUserVerificationId(),
                UserVerificationStatus.PENDING
        );
    }
}

