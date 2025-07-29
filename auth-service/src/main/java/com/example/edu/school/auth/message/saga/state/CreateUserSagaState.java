package com.example.edu.school.auth.message.saga.state;

import com.example.edu.school.auth.message.command.CreateUserCommand;
import com.example.edu.school.auth.message.command.DeleteUserCommand;
import com.example.edu.school.library.enumeration.Gender;
import com.example.edu.school.library.enumeration.Role;
import com.example.edu.school.library.utils.BaseResponse;
import io.eventuate.tram.commands.consumer.CommandWithDestination;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Builder
@Setter
@AllArgsConstructor
public class CreateUserSagaState {
    private String accountId;
    private String email;
    private String firstName;
    private String middleName;
    private String lastName;
    private String phoneNumber;
    private String address;
    private Role role;
    private Gender gender;
    private LocalDate dateOfBirth;

    public CreateUserCommand makeCreateUserCommand() {
        return CreateUserCommand.builder()
                .accountId(getAccountId())
                .email(getEmail())
                .firstName(getFirstName())
                .middleName(getMiddleName())
                .lastName(getLastName())
                .phoneNumber(getPhoneNumber())
                .address(getAddress())
                .role(getRole())
                .gender(getGender())
                .dateOfBirth(getDateOfBirth())
                .build();
    }

    public DeleteUserCommand makeDeleteUserCommand() {
        return DeleteUserCommand.builder()
                .accountId(getAccountId())
                .build();
    }

    public void handleCreateUserReply(BaseResponse<Void> baseResponse) {
        return;
    }
}
