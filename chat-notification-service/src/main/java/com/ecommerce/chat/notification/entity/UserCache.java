package com.ecommerce.chat.notification.entity;

import com.ecommerce.library.enumeration.AccountStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "user_caches")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class UserCache extends BaseEntity {

    @Id
    @JsonProperty("userId")
    private String _id;

    @Field("avatarUrl")
    private String avatarUrl;

    @Field("fullName")
    private String fullName;

    @Field("email")
    private String email;

    @Field("phoneNumber")
    private String phoneNumber;

    @Field("accountStatus")
    private AccountStatus accountStatus;

    @Field("isOnline")
    private Boolean isOnline;
}
