package com.ecommerce.saga.saga.workflow.impl;


import com.ecommerce.saga.saga.activities.CreateUserActivities;
import com.ecommerce.saga.saga.data.CreateUserData;
import com.ecommerce.saga.saga.workflow.CreateUserWorkFlow;
import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.workflow.Saga;
import io.temporal.workflow.Workflow;

import java.time.Duration;

public class CreateUserWorkFlowImpl implements CreateUserWorkFlow {
    private final ActivityOptions options =
            ActivityOptions.newBuilder()
                    .setStartToCloseTimeout(Duration.ofMinutes(5))
                    .setScheduleToCloseTimeout(Duration.ofMinutes(10))
                    .setRetryOptions(RetryOptions.newBuilder().setMaximumAttempts(1).build())
                    .build();
    private final CreateUserActivities activities =
            Workflow.newActivityStub(CreateUserActivities.class, options);

    @Override
    public void createUser(CreateUserData createUserData) {
        Saga.Options sagaOptions = new Saga.Options.Builder().setParallelCompensation(true).build();
        Saga saga = new Saga(sagaOptions);
        try {
            // Bước 1: Tạo người dùng trong user-service
            createUserData = activities.createUser(createUserData);
            saga.addCompensation(activities::deleteUser, createUserData.getUserId());

            // Bước 2: Tạo tài khoản trong auth-service
            createUserData = activities.createAccount(createUserData);
            saga.addCompensation(activities::deleteAccount, createUserData);

            // Bước 3: Đồng bộ sang read-service
            activities.createUserView(createUserData);
        } catch (Exception e) {
            saga.compensate();
            throw e;
        }
    }
}
