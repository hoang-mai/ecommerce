package com.example.edu.school.auth.grpc;


import com.example.edu.school.auth.AccountServiceGrpc;
import com.example.edu.school.auth.ReqCreateAccountDTO;
import com.example.edu.school.auth.ReqDeleteAccountDTO;
import com.example.edu.school.auth.ResCreateAccountDTO;
import com.example.edu.school.auth.service.KeyCloakService;
import com.example.edu.school.library.component.MessageService;
import com.example.edu.school.library.utils.MessageSuccess;
import com.example.edu.school.utils.base_response.BaseResponse;
import com.google.protobuf.Any;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import org.springframework.grpc.server.service.GrpcService;

@GrpcService
@RequiredArgsConstructor
public class GrpcAccountService extends AccountServiceGrpc.AccountServiceImplBase {

    private final KeyCloakService keyCloakService;
    private final MessageService messageService;

    @Override
    public void createAccount(ReqCreateAccountDTO reqCreateAccountDTO, StreamObserver<BaseResponse> baseResponseStreamObserver){
        String accountId = keyCloakService.register(reqCreateAccountDTO);
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
