package com.ecommerce.read.entity;

import com.ecommerce.library.enumeration.AccountStatus;
import com.ecommerce.library.enumeration.Role;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;

@Document("user_views")
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class UserView extends BaseEntity {
    @Id
    @Field(name = "_id")
    @JsonProperty("userId")
    private String _id;

    @Field(name = "username")
    private String username;

    @Field(name = "email")
    private String email;

    @Field(name = "accountStatus")
    private AccountStatus accountStatus;

    @Field(name = "avatarUrl")
    private String avatarUrl;

    @Field(name = "fullName")
    private String fullName;

    @Field(name = "phoneNumber")
    private String phoneNumber;

    @Field(name = "role")
    private Role role;

}
