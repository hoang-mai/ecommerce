package com.example.edu.school.auth.saga.workflow;

import com.example.edu.school.auth.dto.account.ReqCreateAccountDTO;
import com.example.edu.school.auth.saga.data.CreateUserData;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface UserCreateWorkFlow {
    @WorkflowMethod
    void createAccount(CreateUserData createUserData);
}
