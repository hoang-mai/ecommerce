package com.example.app.chat.user.dto.information;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.example.app.chat.library.enumeration.Gender;
import com.example.app.chat.library.enumeration.Role;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Setter
@Getter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {

    private Long userId;

    private String email;

    private String codeNumber;

    private String firstName;

    private String middleName;

    private String lastName;

    private String address;

    private String phoneNumber;

    private String avatarUrl;

    private LocalDate dateOfBirth;


    private Role role;

    private Gender gender;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public UserResponse(
        Long userId,
        String firstName,
        String middleName,
        String lastName,
        String phoneNumber,
        String email,
        Role role,
        Gender gender,
        LocalDate dateOfBirth,
        String codeNumber,
        String address,
        String avatarUrl,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
    ) {
        this.userId = userId;
        this.email = email;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.codeNumber = codeNumber;
        this.address = address;
        this.avatarUrl = avatarUrl;
        this.dateOfBirth = dateOfBirth;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.role = role;
        this.gender = gender;
    }
}
