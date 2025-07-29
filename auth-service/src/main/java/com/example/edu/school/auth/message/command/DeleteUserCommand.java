package com.example.edu.school.auth.message.command;

import io.eventuate.tram.commands.common.Command;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class DeleteUserCommand implements Command {
    private String accountId;
}
