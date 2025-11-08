package com.ecommerce.auth.entity;

import com.ecommerce.library.entity.BaseEntity;
import com.ecommerce.library.enumeration.AccountStatus;
import com.ecommerce.library.enumeration.Role;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "accounts")
public class Account extends BaseEntity {

    @Id
    @Column(name = "user_id", unique = true, nullable = false)
    private Long userId;

    @Column(name = "account_id", unique = true, nullable = false)
    private String accountId;

    @Column(name = "username", unique = true, nullable = false)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "account_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private AccountStatus accountStatus;

}
