package com.ecommerce.notification.entity;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "notifications")
public class Notification {

    @Id
    @Field("_id")
    private String _id;

    private Long userId;

    @Field("title")
    private String title;

    @Field("message")
    private String message;

    @Field("notificationType")
    private NotificationType notificationType;


    @Field("isRead")
    private Boolean isRead;

    @Field("sentRealtime")
    private Boolean sentRealtime;

    @Field("createdAt")
    @CreatedDate
    private LocalDateTime createdAt;

    @Field("updatedAt")
    @LastModifiedDate
    private LocalDateTime updatedAt;

}
