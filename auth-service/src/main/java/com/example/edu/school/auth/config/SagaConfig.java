package com.example.edu.school.auth.config;

import com.example.edu.school.auth.message.command.handler.AuthCommandHandler;
import com.example.edu.school.auth.message.saga.CreateUserSaga;
import com.example.edu.school.auth.message.saga.state.CreateUserSagaState;
import io.eventuate.tram.commands.producer.CommandProducer;
import io.eventuate.tram.messaging.consumer.MessageConsumer;
import io.eventuate.tram.sagas.common.SagaLockManager;
import io.eventuate.tram.sagas.orchestration.SagaCommandProducer;
import io.eventuate.tram.sagas.orchestration.SagaInstanceRepository;
import io.eventuate.tram.sagas.orchestration.SagaManager;
import io.eventuate.tram.sagas.orchestration.SagaManagerImpl;
import io.eventuate.tram.sagas.participant.SagaCommandDispatcher;
import io.eventuate.tram.sagas.participant.SagaCommandDispatcherFactory;
import io.eventuate.tram.sagas.spring.orchestration.SagaOrchestratorConfiguration;
import io.eventuate.tram.sagas.spring.participant.SagaParticipantConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({SagaParticipantConfiguration.class, SagaOrchestratorConfiguration.class})
public class SagaConfig {

    @Bean
    public SagaManager<CreateUserSagaState> createUserSagaManager(
            CreateUserSaga createUserSaga,
            SagaInstanceRepository sagaInstanceRepository,
            CommandProducer commandProducer,
            MessageConsumer messageConsumer,
            SagaCommandProducer sagaCommandProducer,
            SagaLockManager sagaLockManager) {

        return new SagaManagerImpl<>(
                createUserSaga,
                sagaInstanceRepository,
                commandProducer,
                messageConsumer,
                sagaLockManager,
                sagaCommandProducer
        );
    }

    @Bean
    public SagaCommandDispatcher authCommandHandlersDispatcher(
            SagaCommandDispatcherFactory sagaCommandDispatcherFactory,
            AuthCommandHandler authCommandHandler) {

        return sagaCommandDispatcherFactory.make("authServiceChannel", authCommandHandler.commandHandlers());
    }
}
