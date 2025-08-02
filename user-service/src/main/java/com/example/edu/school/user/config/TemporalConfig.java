package com.example.edu.school.user.config;

import com.example.edu.school.library.utils.Constant;
import com.example.edu.school.user.saga.activities.impl.CreateUserActivitiesImpl;
import com.example.edu.school.user.saga.workflow.impl.CreateUserWorkFlowImpl;
import io.temporal.client.WorkflowClient;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TemporalConfig {

    @Bean
    public WorkflowClient workflowClient() {
        WorkflowServiceStubs service = WorkflowServiceStubs.newLocalServiceStubs();
        return WorkflowClient.newInstance(service);
    }

    @Bean
    public WorkerFactory workerFactory(WorkflowClient client) {
        return WorkerFactory.newInstance(client);
    }

    @PostConstruct
    public void startWorkers(WorkerFactory workerFactory, CreateUserActivitiesImpl createUserActivitiesImpl) {
        Worker worker = workerFactory.newWorker(Constant.CREATE_USER_QUEUE);
        worker.registerWorkflowImplementationTypes(CreateUserWorkFlowImpl.class);
        worker.registerActivitiesImplementations(createUserActivitiesImpl);
        workerFactory.start();
    }
}
