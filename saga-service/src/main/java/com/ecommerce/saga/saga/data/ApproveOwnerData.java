package com.ecommerce.saga.saga.data;

import com.ecommerce.library.enumeration.Role;
import com.ecommerce.library.enumeration.UserVerificationStatus;
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
    private Role newRole;
    private UserVerificationStatus oldUserVerificationStatus;
    private UserVerificationStatus newUserVerificationStatus;
    private String token;
}

