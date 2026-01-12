package com.ecommerce.user.dto;

import java.time.Instant;
import java.time.LocalDate;

import com.ecommerce.library.enumeration.Gender;
import com.ecommerce.library.enumeration.Role;

import lombok.*;

@Getter
@Builder
public class ResInfoUserDTO {
    private Long userId;
    private String email;
    private String description;
    private String fullName;
    private LocalDate dateOfBirth;
    private String phoneNumber;
    private String avatarUrl;
    private Role role;
    private Gender gender;
    private Instant createdAt;
    private Instant updatedAt;
    private Boolean isVerification;
}
