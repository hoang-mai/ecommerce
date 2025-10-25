package com.ecommerce.user.saga.workflow;

import com.ecommerce.user.saga.data.CreateUserData;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface CreateUserWorkFlow {
    @WorkflowMethod
    void createUser(CreateUserData createUserData);
}
