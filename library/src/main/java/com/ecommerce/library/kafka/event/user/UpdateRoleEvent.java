package com.ecommerce.library.kafka.event.user;

import com.ecommerce.library.enumeration.Role;
import lombok.*;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateRoleEvent {
    private Long userId;
    private Role role;
}
