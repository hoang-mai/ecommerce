package com.ecommerce.auth.grpc;


import com.ecommerce.auth.AccountServiceGrpc;
import com.ecommerce.auth.ReqCreateAccountDTO;
import com.ecommerce.auth.ReqDeleteAccountDTO;
import com.ecommerce.auth.ResCreateAccountDTO;
import com.ecommerce.auth.service.AuthService;
import com.ecommerce.auth.service.KeyCloakService;
import com.ecommerce.library.component.MessageService;
import com.ecommerce.library.utils.MessageSuccess;
import com.ecommerce.utils.base_response.BaseResponse;
import com.google.protobuf.Any;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import org.springframework.grpc.server.service.GrpcService;

@GrpcService
@RequiredArgsConstructor
public class GrpcAccountService extends AccountServiceGrpc.AccountServiceImplBase {

    private final KeyCloakService keyCloakService;
    private final AuthService authService;
    private final MessageService messageService;

    @Override
    public void createAccount(ReqCreateAccountDTO reqCreateAccountDTO, StreamObserver<BaseResponse> baseResponseStreamObserver){
        String accountId = keyCloakService.register(reqCreateAccountDTO);
        authService.createAccount(reqCreateAccountDTO, accountId);
        ResCreateAccountDTO resCreateAccountDTO = ResCreateAccountDTO.newBuilder()
                .setAccountId(accountId)
                .build();
        BaseResponse baseResponse = BaseResponse.newBuilder()
                .setStatusCode(201)
                .setMessage(messageService.getMessage(MessageSuccess.ACCOUNT_CREATED_SUCCESS))
                .setData(Any.pack(resCreateAccountDTO))
                .build();
        baseResponseStreamObserver.onNext(baseResponse);
        baseResponseStreamObserver.onCompleted();
    }

    @Override
    public void deleteAccount(ReqDeleteAccountDTO reqDeleteAccountDTO, StreamObserver<BaseResponse> baseResponseStreamObserver) {
        keyCloakService.delete(reqDeleteAccountDTO.getAccountId());
        BaseResponse baseResponse = BaseResponse.newBuilder()
                .setStatusCode(200)
                .setMessage(messageService.getMessage(MessageSuccess.ACCOUNT_DELETED_SUCCESS))
                .build();
        baseResponseStreamObserver.onNext(baseResponse);
        baseResponseStreamObserver.onCompleted();
    }
}
