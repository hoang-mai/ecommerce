package com.example.edu.school.user.saga.workflow.impl;


import com.example.edu.school.user.dto.user.ReqCreateUserDTO;
import com.example.edu.school.user.saga.activities.CreateUserActivities;
import com.example.edu.school.user.saga.data.CreateUserData;
import com.example.edu.school.user.saga.workflow.CreateUserWorkFlow;
import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.failure.ActivityFailure;
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
    public String createUser(CreateUserData createUserData) {
        Saga.Options sagaOptions = new Saga.Options.Builder().setParallelCompensation(true).build();
        Saga saga = new Saga(sagaOptions);
        try {
            createUserData = activities.createUser(createUserData);
            saga.addCompensation(activities::deleteUser, createUserData.getUserId());

            String accountId = activities.createAccount(createUserData);
            saga.addCompensation(activities::deleteAccount, accountId);
            return createUserData.getEmail();
        } catch (ActivityFailure e) {
            saga.compensate();
            throw e;
        }
    }
}
