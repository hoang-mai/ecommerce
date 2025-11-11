package com.ecommerce.saga.saga.workflow.impl;

import com.ecommerce.saga.saga.activities.ApproveOwnerActivities;
import com.ecommerce.saga.saga.data.ApproveOwnerData;
import com.ecommerce.saga.saga.workflow.ApproveOwnerWorkFlow;
import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.workflow.Saga;
import io.temporal.workflow.Workflow;

import java.time.Duration;

public class ApproveOwnerWorkFlowImpl implements ApproveOwnerWorkFlow {

    private final ActivityOptions options =
            ActivityOptions.newBuilder()
                    .setStartToCloseTimeout(Duration.ofMinutes(5))
                    .setScheduleToCloseTimeout(Duration.ofMinutes(10))
                    .setRetryOptions(RetryOptions.newBuilder().setMaximumAttempts(1).build())
                    .build();

    private final ApproveOwnerActivities activities =
            Workflow.newActivityStub(ApproveOwnerActivities.class, options);

    @Override
    public void approveOwner(ApproveOwnerData approveOwnerData) {
        Saga.Options sagaOptions = new Saga.Options.Builder()
                .setParallelCompensation(false)
                .build();
        Saga saga = new Saga(sagaOptions);

        try {
            // Bước 1: Cập nhật role của user trong user-service và verification status thành APPROVED
            approveOwnerData = activities.updateUserRoleAndVerificationStatus(approveOwnerData);
            saga.addCompensation(activities::rollbackUserRoleAndVerificationStatus, approveOwnerData);

            // Bước 2: Cập nhật role của account trong auth-service
            activities.updateAccountRole(approveOwnerData);
            saga.addCompensation(activities::rollbackAccountRole, approveOwnerData);

            // Bước 3: Đồng bộ sang read-service
            activities.updateUserRole(approveOwnerData);
        } catch (Exception e) {
            saga.compensate();
            throw e;
        }
    }
}

