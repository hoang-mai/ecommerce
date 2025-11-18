package com.ecommerce.read.entity;

import com.ecommerce.library.enumeration.AccountStatus;
import com.ecommerce.library.enumeration.Role;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Document("user_views")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserView {
    @Id
    @Field(name = "user_id")
    private String userId;

    @Field(name = "username")
    private String username;

    @Field(name = "email")
    private String email;

    @Field(name = "account_status")
    private AccountStatus accountStatus;

    @Field(name = "avatar_url")
    private String avatarUrl;

    @Field(name = "first_name")
    private String firstName;

    @Field(name = "middle_name")
    private String middleName;

    @Field(name = "last_name")
    private String lastName;

    @Field(name = "phone_number")
    private String phoneNumber;

    @Field(name = "role")
    private Role role;

    @CreatedDate
    @Field(name = "created_at")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Field(name = "updated_at")
    private LocalDateTime updatedAt;
}
