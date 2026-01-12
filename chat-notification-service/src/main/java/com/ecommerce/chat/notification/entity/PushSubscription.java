package com.ecommerce.chat.notification.entity;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;

@Document(collection = "push_subscriptions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PushSubscription {

    @Id
    private String _id;

    @Field("userId")
    private Long userId;

    private String endpoint;

    @Field("p256dh")
    private String p256dh;

    @Field("auth")
    private String auth;

    @Field("active")
    private boolean active;

    @Field("createdAt")
    @CreatedDate
    private Instant createdAt;

    @Field("updatedAt")
    @LastModifiedDate
    private Instant updatedAt;
}
