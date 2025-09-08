package com.example.app.chat.user.saga.data;

import com.example.app.chat.library.enumeration.Gender;
import com.example.app.chat.library.enumeration.Role;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserData {
    private String accountId;
    private Long userId;
    private String username;
    private String password;
    private String email;
    private String firstName;
    private String middleName;
    private String lastName;
    private String phoneNumber;
    private String address;
    private Gender gender;
    private LocalDate dateOfBirth;
}
