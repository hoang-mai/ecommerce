package com.ecommerce.user.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


import com.ecommerce.library.entity.BaseEntity;
import com.ecommerce.library.enumeration.Gender;
import com.ecommerce.library.enumeration.Role;
import jakarta.persistence.*;
import lombok.*;


@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "description")
    private String description;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "middle_name")
    private String middleName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "phone_number", unique = true)
    private String phoneNumber;

    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(name = "gender")
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(name = "avatar_url", length = 1000)
    private String avatarUrl;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Address> addresses;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<UserVerification> userVerifications;
}