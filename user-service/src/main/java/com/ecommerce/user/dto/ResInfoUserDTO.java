package com.ecommerce.user.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.ecommerce.library.enumeration.Gender;
import com.ecommerce.library.enumeration.Role;

import lombok.*;

@Getter
@Builder
public class ResInfoUserDTO {
    private Long userId;
    private String email;
    private String description;
    private String firstName;
    private String middleName;
    private String lastName;
    private LocalDate dateOfBirth;
    private String phoneNumber;
    private String avatarUrl;
    private Role role;
    private Gender gender;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean isVerification;
}
