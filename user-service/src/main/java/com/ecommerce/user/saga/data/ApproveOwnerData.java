package com.ecommerce.user.saga.data;

import com.ecommerce.library.enumeration.Role;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApproveOwnerData {
    private Long userVerificationId;
    private Long userId;
    private Role oldRole;
    private String token;
}

