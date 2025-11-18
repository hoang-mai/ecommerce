package com.ecommerce.read.dto;

import com.ecommerce.library.enumeration.AccountStatus;
import com.ecommerce.library.enumeration.Role;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * DTO for {@link com.ecommerce.read.entity.UserView}
 */
@Getter
@Builder
public class UserViewDto {
    private final Long userId;
    private final String username;
    private final String email;
    private final AccountStatus accountStatus;
    private final String firstName;
    private final String middleName;
    private final String lastName;
    private final String phoneNumber;
    private final Role role;
    private final String avatarUrl;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
}