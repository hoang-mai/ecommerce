package com.ecommerce.library.kafka.event.user;

import com.ecommerce.library.enumeration.AccountStatus;
import com.ecommerce.library.enumeration.Role;
import lombok.*;

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
    private String firstName;
    private String middleName;
    private String lastName;
    private String phoneNumber;
    private Role role;
}
