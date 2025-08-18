package com.example.app.chat.user.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.example.app.chat.library.enumeration.Gender;
import com.example.app.chat.library.enumeration.Role;

import lombok.*;

@Getter
@Builder
public class ResInfoUserDTO {
    private Long userId;
    private String email;
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
}
