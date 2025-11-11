package com.ecommerce.saga.saga.workflow;

import com.ecommerce.saga.saga.data.ApproveOwnerData;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface ApproveOwnerWorkFlow {
    @WorkflowMethod
    void approveOwner(ApproveOwnerData approveOwnerData);
}

