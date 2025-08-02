package com.example.edu.school.user.saga.data;

import com.example.edu.school.library.enumeration.Gender;
import com.example.edu.school.library.enumeration.Role;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Builder
public class CreateUserData {
    private String accountId;
    private Long userId;
    private String email;
    private String password;
    private String firstName;
    private String middleName;
    private String lastName;
    private String phoneNumber;
    private String address;
    private Role role;
    private Gender gender;
    private LocalDate dateOfBirth;
}
