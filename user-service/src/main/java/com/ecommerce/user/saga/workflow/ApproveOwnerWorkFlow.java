package com.ecommerce.user.saga.workflow;

import com.ecommerce.user.saga.data.ApproveOwnerData;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface ApproveOwnerWorkFlow {
    @WorkflowMethod
    void approveOwner(ApproveOwnerData approveOwnerData);
}

