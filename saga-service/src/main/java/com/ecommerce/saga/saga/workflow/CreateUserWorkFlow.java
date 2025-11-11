package com.ecommerce.saga.saga.workflow;

import com.ecommerce.saga.saga.data.CreateUserData;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface CreateUserWorkFlow {
    @WorkflowMethod
    void createUser(CreateUserData createUserData);
}
