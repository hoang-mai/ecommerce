package com.example.edu.school.auth.message.saga;

import com.example.edu.school.auth.message.saga.proxy.AuthServiceProxy;
import com.example.edu.school.auth.message.saga.proxy.UserServiceProxy;
import com.example.edu.school.auth.message.saga.state.CreateUserSagaState;
import com.example.edu.school.library.utils.BaseResponse;
import io.eventuate.tram.sagas.orchestration.SagaDefinition;
import io.eventuate.tram.sagas.simpledsl.SimpleSaga;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CreateUserSaga implements SimpleSaga<CreateUserSagaState> {

    private final AuthServiceProxy authServiceProxy;
    private final UserServiceProxy userServiceProxy;

    @Override
    public SagaDefinition<CreateUserSagaState> getSagaDefinition() {
        return step()
                .invokeParticipant(authServiceProxy.delete,
                        CreateUserSagaState::makeDeleteUserCommand)
                .step()
                .invokeParticipant(
                        userServiceProxy.create,
                        CreateUserSagaState::makeCreateUserCommand
                )
                .onReply(BaseResponse.class, CreateUserSagaState::handleCreateUserReply)
                .withCompensation(
                        userServiceProxy.delete,
                        CreateUserSagaState::makeDeleteUserCommand
                )
                .build();
    }
}
