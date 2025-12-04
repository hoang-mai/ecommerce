package com.ecommerce.chat.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Document(collection = "users")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User extends BaseEntity {

    @Id
    @Field("_id")
    private String id;

    private String avatarUrl;

    @Field("full_name")
    private String fullName;

    @Field("email")
    private String email;

    @Field("phone_number")
    private String phoneNumber;

    @Field("is_online")
    private Boolean isOnline;

    @Field("last_seen")
    private LocalDateTime lastSeen;

    @Field("user_type")
    private UserType userType;

    @Field("is_active")
    private Boolean isActive;

    public enum UserType {
        CUSTOMER,
        SHOP_OWNER,
        ADMIN
    }

}
