package com.ecommerce.saga.service.impl;

import com.ecommerce.library.component.UserHelper;
import com.ecommerce.library.enumeration.AccountStatus;
import com.ecommerce.library.enumeration.Role;
import com.ecommerce.library.enumeration.UserVerificationStatus;
import com.ecommerce.library.exception.HttpRequestException;
import com.ecommerce.library.utils.Constant;
import com.ecommerce.saga.dto.ReqCreateUserDTO;
import com.ecommerce.saga.saga.data.ApproveOwnerData;
import com.ecommerce.saga.saga.data.CreateUserData;
import com.ecommerce.saga.saga.workflow.ApproveOwnerWorkFlow;
import com.ecommerce.saga.saga.workflow.CreateUserWorkFlow;
import com.ecommerce.saga.service.SagaService;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowFailedException;
import io.temporal.client.WorkflowOptions;
import io.temporal.failure.ApplicationFailure;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SagaServiceImpl implements SagaService {

    private final WorkflowClient workflowClient;
    private final UserHelper userHelper;

    @Override
    public void createUser(ReqCreateUserDTO reqCreateUserDTO) {
        try {
            WorkflowOptions options = WorkflowOptions.newBuilder().setTaskQueue(Constant.CREATE_USER_QUEUE).build();
            CreateUserWorkFlow createUserWorkFlow = workflowClient.newWorkflowStub(CreateUserWorkFlow.class, options);
            createUserWorkFlow.createUser(CreateUserData.builder()
                    .username(reqCreateUserDTO.getUsername())
                    .password(reqCreateUserDTO.getPassword())
                    .firstName(reqCreateUserDTO.getFirstName())
                    .middleName(reqCreateUserDTO.getMiddleName())
                    .lastName(reqCreateUserDTO.getLastName())
                    .phoneNumber(reqCreateUserDTO.getPhoneNumber())
                    .gender(reqCreateUserDTO.getGender())
                    .ward(reqCreateUserDTO.getWard())
                    .province(reqCreateUserDTO.getProvince())
                    .detail(reqCreateUserDTO.getDetail())
                    .receiverName(reqCreateUserDTO.getReceiverName())
                    .role(Role.USER)
                    .isDefault(true)
                    .accountStatus(AccountStatus.ACTIVE)
                    .build());
        } catch (WorkflowFailedException e) {
            if (e.getCause().getCause() instanceof ApplicationFailure failure) {
                throw new HttpRequestException(failure.getOriginalMessage(), HttpStatus.BAD_REQUEST.value(), LocalDateTime.now());

            }
            throw new HttpRequestException(e.getCause().getCause().getLocalizedMessage(), HttpStatus.BAD_REQUEST.value(), LocalDateTime.now());
        }

    }

    @Override
    public void approveOwnerRequest(Long userVerificationId) {
        try {
            WorkflowOptions options = WorkflowOptions.newBuilder().setTaskQueue(Constant.APPROVE_OWNER_QUEUE).build();
            ApproveOwnerWorkFlow approveOwnerWorkFlow = workflowClient.newWorkflowStub(ApproveOwnerWorkFlow.class, options);

            approveOwnerWorkFlow.approveOwner(ApproveOwnerData.builder()
                    .userVerificationId(userVerificationId)
                    .token(userHelper.getToken())
                    .newRole(Role.OWNER)
                    .newUserVerificationStatus(UserVerificationStatus.APPROVED)
                    .build());

        } catch (WorkflowFailedException e) {
            if (e.getCause().getCause() instanceof ApplicationFailure failure) {
                throw new HttpRequestException(
                        failure.getOriginalMessage(),
                        HttpStatus.BAD_REQUEST.value(),
                        LocalDateTime.now()
                );
            }
            throw new HttpRequestException(
                    e.getCause().getCause().getLocalizedMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    LocalDateTime.now()
            );
        }
    }
}
