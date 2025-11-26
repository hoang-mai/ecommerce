package com.ecommerce.read.entity;

import com.ecommerce.library.enumeration.AccountStatus;
import com.ecommerce.library.enumeration.Role;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Document("user_views")
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class UserView extends BaseEntity {
    @Id
    @Field(name = "_id")
    private String _id;

    @Field(name = "username")
    private String username;

    @Field(name = "email")
    private String email;

    @Field(name = "accountStatus")
    private AccountStatus accountStatus;

    @Field(name = "avatarUrl")
    private String avatarUrl;

    @Field(name = "firstName")
    private String firstName;

    @Field(name = "middleName")
    private String middleName;

    @Field(name = "lastName")
    private String lastName;

    @Field(name = "phoneNumber")
    private String phoneNumber;

    @Field(name = "role")
    private Role role;

}
