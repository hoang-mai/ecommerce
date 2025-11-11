package com.ecommerce.read.entity;

import com.ecommerce.library.entity.BaseEntity;
import com.ecommerce.library.enumeration.AccountStatus;
import com.ecommerce.library.enumeration.Role;
import jakarta.persistence.*;
import lombok.*;

@Table(name = "user_views")
@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserView extends BaseEntity {
    @Id
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "username", unique = true)
    private String username;

    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "account_status")
    @Enumerated(EnumType.STRING)
    private AccountStatus accountStatus;

    @Column(name = "avatar_url")
    private String avatarUrl;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "middle_name")
    private String middleName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private Role role;
}
