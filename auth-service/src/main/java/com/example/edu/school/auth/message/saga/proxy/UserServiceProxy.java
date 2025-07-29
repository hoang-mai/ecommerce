package com.example.edu.school.auth.message.saga.proxy;

import com.example.edu.school.auth.message.command.CreateUserCommand;
import com.example.edu.school.auth.message.command.DeleteUserCommand;
import com.example.edu.school.library.utils.BaseResponse;
import io.eventuate.tram.sagas.simpledsl.CommandEndpoint;
import io.eventuate.tram.sagas.simpledsl.CommandEndpointBuilder;
import org.springframework.stereotype.Component;

@Component
public class UserServiceProxy {
    public final CommandEndpoint<CreateUserCommand> create =
            CommandEndpointBuilder
                    .forCommand(CreateUserCommand.class)
                    .withChannel("userServiceChannel")
                    .withReply(BaseResponse.class)
                    .build();

    public final CommandEndpoint<DeleteUserCommand> delete =
            CommandEndpointBuilder
                    .forCommand(DeleteUserCommand.class)
                    .withChannel("userServiceChannel")
                    .withReply(BaseResponse.class)
                    .build();
}
