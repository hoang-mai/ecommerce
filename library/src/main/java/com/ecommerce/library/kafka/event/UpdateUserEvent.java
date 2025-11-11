package com.ecommerce.library.kafka.event;

import com.ecommerce.library.enumeration.AccountStatus;
import com.ecommerce.library.enumeration.Role;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserEvent {
    private Long userId;
    private String firstName;
    private String middleName;
    private String lastName;
    private String email;
    private String phoneNumber;
}
