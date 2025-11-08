package com.ecommerce.user.grpc;

import com.ecommerce.library.component.MessageService;
import com.ecommerce.library.utils.MessageSuccess;
import com.ecommerce.user.ReqGetUserChatDTO;
import com.ecommerce.user.ResGetUserChatDTO;
import com.ecommerce.user.UserServiceGrpc;
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

    public void getUserChatById(ReqGetUserChatDTO reqGetUserChatDTO, StreamObserver<BaseResponse> baseResponseStreamObserver) {
        ResInfoUserDTO resInfoUserDTO = userService.getUserById(reqGetUserChatDTO.getUserId());

        ResGetUserChatDTO resGetUserChatDTO = ResGetUserChatDTO.newBuilder()
                .setUserId(resInfoUserDTO.getUserId())
                .setFirstName(resInfoUserDTO.getFirstName())
                .setMiddleName(resInfoUserDTO.getMiddleName())
                .setLastName(resInfoUserDTO.getLastName())
                .setAvatarUrl(resInfoUserDTO.getAvatarUrl())
                .build();
        BaseResponse baseResponse = BaseResponse.newBuilder()
                .setStatusCode(201)
                .setMessage(messageService.getMessage(MessageSuccess.GET_INFO_USER_SUCCESS))
                .setData(Any.pack(resGetUserChatDTO))
                .build();
        baseResponseStreamObserver.onNext(baseResponse);
        baseResponseStreamObserver.onCompleted();
    }
}
