package com.ecommerce.library.kafka.event.user;

import com.ecommerce.library.enumeration.AccountStatus;
import com.ecommerce.library.enumeration.Role;
import lombok.*;

import java.time.Instant;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CreateUserEvent {
    private Long userId;
    private String username;
    private String password;
    private String email;
    private AccountStatus accountStatus;
    private String fullName;
    private String phoneNumber;
    private Role role;
    private Instant createdAt;
    private Instant updatedAt;
}
