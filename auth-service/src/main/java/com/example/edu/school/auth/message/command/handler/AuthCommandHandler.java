package com.example.edu.school.auth.message.command.handler;

import com.example.edu.school.auth.message.command.DeleteUserCommand;
import com.example.edu.school.auth.service.AccountService;
import io.eventuate.tram.commands.consumer.CommandHandlers;
import io.eventuate.tram.commands.consumer.CommandMessage;
import io.eventuate.tram.messaging.common.Message;
import io.eventuate.tram.sagas.participant.SagaCommandHandlersBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static io.eventuate.tram.commands.consumer.CommandHandlerReplyBuilder.withSuccess;

@Component
@RequiredArgsConstructor
public class AuthCommandHandler {
    private final AccountService accountService;
    public CommandHandlers commandHandlers() {
        return SagaCommandHandlersBuilder
                .fromChannel("authServiceChannel")
                .onMessage(DeleteUserCommand.class, this::deleteAccount)
                .build();
    }

    public Message deleteAccount(CommandMessage<DeleteUserCommand> deleteUserCommandCommandMessage) {
        String accountId = deleteUserCommandCommandMessage.getCommand().getAccountId();
        accountService.deleteAccount(accountId);
        return withSuccess();
    }
}
