package com.ecommerce.user.grpc;

import com.ecommerce.auth.ResCreateAccountDTO;
import com.ecommerce.library.component.MessageService;
import com.ecommerce.library.utils.MessageSuccess;
import com.ecommerce.user.*;
import com.ecommerce.user.dto.ResInfoUserDTO;
import com.ecommerce.user.service.UserService;
import com.ecommerce.utils.base_response.BaseResponse;
import com.google.protobuf.Any;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import org.springframework.grpc.server.service.GrpcService;

@GrpcService
@RequiredArgsConstructor
public class GrpcUserService extends UserServiceGrpc.UserServiceImplBase {

    private final UserService userService;
    private final MessageService messageService;

    @Override
    public void createUser(ReqCreateUserDTO reqCreateUserDTO, StreamObserver<BaseResponse> responseObserver) {
        Long userId = userService.createUser(reqCreateUserDTO);
        ResCreateUserDTO resCreateAccountDTO = ResCreateUserDTO.newBuilder()
                .setUserId(userId)
                .build();
        BaseResponse response = BaseResponse.newBuilder()
                .setStatusCode(201)
                .setMessage(messageService.getMessage(MessageSuccess.USER_CREATED_SUCCESS))
                .setData(Any.pack(resCreateAccountDTO))
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void deleteUser(ReqDeleteUserDTO reqDeleteUserDTO, StreamObserver<BaseResponse> responseObserver) {
        userService.deleteUser(reqDeleteUserDTO.getUserId());
        BaseResponse baseResponse = BaseResponse.newBuilder()
                .setStatusCode(200)
                .setMessage(messageService.getMessage(MessageSuccess.ACCOUNT_DELETED_SUCCESS))
                .build();
        responseObserver.onNext(baseResponse);
        responseObserver.onCompleted();
    }

    @Override
    public void updateUserRoleAndVerificationStatus(ReqUpdateUserRoleAndVerificationStatusDTO reqUpdateUserRoleAndVerificationStatusDTO, StreamObserver<BaseResponse> responseObserver) {
        ResUpdateUserRoleAndVerificationStatusDTO resUpdateUserRoleAndVerificationStatusDTO =  userService.updateUserRoleAndVerificationStatus(reqUpdateUserRoleAndVerificationStatusDTO);
        BaseResponse baseResponse = BaseResponse.newBuilder()
                .setStatusCode(200)
                .setMessage(messageService.getMessage(MessageSuccess.UPDATE_USER_ROLE_SUCCESS))
                .setData(Any.pack(resUpdateUserRoleAndVerificationStatusDTO))
                .build();
        responseObserver.onNext(baseResponse);
        responseObserver.onCompleted();
    }



    @Override
    public void rollbackUpdateUserRoleAndVerificationStatus(ReqRollbackUpdateUserRoleAndVerificationStatusDTO reqRollbackUpdateUserRoleAndVerificationStatusDTO, StreamObserver<BaseResponse> responseObserver) {
        userService.rollbackUpdateUserRoleAndVerificationStatus(reqRollbackUpdateUserRoleAndVerificationStatusDTO);
        BaseResponse baseResponse = BaseResponse.newBuilder()
                .setStatusCode(200)
                .setMessage(messageService.getMessage(MessageSuccess.ROLLBACK_UPDATE_USER_ROLE_SUCCESS))
                .build();
        responseObserver.onNext(baseResponse);
        responseObserver.onCompleted();
    }

}
