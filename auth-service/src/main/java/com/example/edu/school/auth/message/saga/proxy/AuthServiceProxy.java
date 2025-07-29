package com.example.edu.school.auth.message.saga.proxy;

import com.example.edu.school.auth.message.command.DeleteUserCommand;
import com.example.edu.school.library.utils.BaseResponse;
import io.eventuate.tram.sagas.simpledsl.CommandEndpoint;
import io.eventuate.tram.sagas.simpledsl.CommandEndpointBuilder;
import org.springframework.stereotype.Component;

@Component
public class AuthServiceProxy {
    public final CommandEndpoint<DeleteUserCommand> delete =
            CommandEndpointBuilder
                    .forCommand(DeleteUserCommand.class)
                    .withChannel("authServiceChannel")
                    .withReply(BaseResponse.class)
                    .build();

}
