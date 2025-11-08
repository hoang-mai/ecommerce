package com.ecommerce.user.dto;

import com.ecommerce.user.enumeration.UserVerificationStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
public class ResUserVerificationDTO {
    private Long userVerificationId;
    private String verificationCode;
    private String avatarUrl;
    private String accountNumber;
    private String bankName;
    private String frontImageUrl;
    private String backImageUrl;
    private String rejectReason;
    private UserVerificationStatus userVerificationStatus;
    private Long userId;
    private String userName;
    private String userEmail;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

