package com.ecommerce.user.config;

import com.ecommerce.library.utils.Constant;
import com.ecommerce.user.saga.activities.impl.CreateUserActivitiesImpl;
import com.ecommerce.user.saga.workflow.impl.CreateUserWorkFlowImpl;
import io.temporal.client.WorkflowClient;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.serviceclient.WorkflowServiceStubsOptions;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TemporalConfig {

    @Value("${temporal.url}")
    private String temporalUrl;

    @Bean
    public WorkflowClient workflowClient() {
        WorkflowServiceStubs service = WorkflowServiceStubs.newServiceStubs(WorkflowServiceStubsOptions.newBuilder().setTarget(temporalUrl).build());
        return WorkflowClient.newInstance(service);
    }

    @Bean
    public WorkerFactory workerFactory(WorkflowClient client, CreateUserActivitiesImpl createUserActivitiesImpl) {
        WorkerFactory workerFactory = WorkerFactory.newInstance(client);
        Worker worker = workerFactory.newWorker(Constant.CREATE_USER_QUEUE);
        worker.registerWorkflowImplementationTypes(CreateUserWorkFlowImpl.class);
        worker.registerActivitiesImplementations(createUserActivitiesImpl);
        workerFactory.start();
        return workerFactory;
    }
}
