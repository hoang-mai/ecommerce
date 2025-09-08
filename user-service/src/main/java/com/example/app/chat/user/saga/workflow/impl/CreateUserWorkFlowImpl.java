package com.example.app.chat.user.saga.workflow.impl;


import com.example.app.chat.library.exception.HttpRequestException;
import com.example.app.chat.library.utils.MessageError;
import com.example.app.chat.user.saga.activities.CreateUserActivities;
import com.example.app.chat.user.saga.data.CreateUserData;
import com.example.app.chat.user.saga.workflow.CreateUserWorkFlow;
import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.failure.ActivityFailure;
import io.temporal.failure.ApplicationFailure;
import io.temporal.workflow.Saga;
import io.temporal.workflow.Workflow;

import java.time.Duration;
import java.time.LocalDateTime;

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
            createUserData = activities.createUser(createUserData);
            saga.addCompensation(activities::deleteUser, createUserData.getUserId());

            createUserData = activities.createAccount(createUserData);
            saga.addCompensation(activities::deleteAccount, createUserData);
        } catch (ActivityFailure e) {
            saga.compensate();
            if (e.getCause() instanceof ApplicationFailure applicationFailure && applicationFailure.getType().equals(HttpRequestException.class.getName())) {
                throw new HttpRequestException(applicationFailure.getMessage(), 500, LocalDateTime.now());
            } else {
                throw new RuntimeException(MessageError.SYSTEM_ERROR);
            }
        }
    }
}
