package com.example.edu.school.auth.client.dto;

import com.example.edu.school.library.enumeration.Gender;
import com.example.edu.school.library.enumeration.Role;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Builder
@Setter
public class ReqCreateUserDTO {
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
}
