package com.ecommerce.user.entity;

import com.ecommerce.library.entity.BaseEntity;
import com.ecommerce.library.enumeration.UserVerificationStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "user_verifications")
public class UserVerification extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "user_verification_id", updatable = false, nullable = false)
    private Long userVerificationId;

    @Column(name = "verification_code", nullable = false, length = 15)
    private String verificationCode;

    @Column(name = "avatar_url", nullable = false, length = 1000)
    private String avatarUrl;

    @Column(name = "account_number", nullable = false, length = 50)
    private String accountNumber;

    @Column(name = "bank_name", nullable = false, length = 100)
    private String bankName;

    @Column(name = "front_image_url", nullable = false, length = 1000)
    private String frontImageUrl;

    @Column(name = "back_image_url", nullable = false, length = 1000)
    private String backImageUrl;

    @Column(name = "reject_reason", length = 500)
    private String rejectReason;

    @Enumerated(EnumType.STRING)
    @Column(name = "use_verification_status", nullable = false, length = 20)
    private UserVerificationStatus userVerificationStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}
