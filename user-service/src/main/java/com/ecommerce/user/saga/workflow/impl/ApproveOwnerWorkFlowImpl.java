package com.ecommerce.user.saga.workflow.impl;

import com.ecommerce.user.saga.activities.ApproveOwnerActivities;
import com.ecommerce.user.saga.data.ApproveOwnerData;
import com.ecommerce.user.saga.workflow.ApproveOwnerWorkFlow;
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
            // Bước 1: Cập nhật role của user trong user-service
            approveOwnerData = activities.updateUserRole(approveOwnerData);
            saga.addCompensation(activities::rollbackUserRole, approveOwnerData);

            // Bước 2: Cập nhật role của account trong auth-service
            activities.updateAccountRole(approveOwnerData);
            saga.addCompensation(activities::rollbackAccountRole, approveOwnerData);

            // Bước 3: Cập nhật trạng thái verification
            activities.updateVerificationStatus(approveOwnerData);
            saga.addCompensation(activities::rollbackVerificationStatus, approveOwnerData);

        } catch (Exception e) {
            saga.compensate();
            throw e;
        }
    }
}

