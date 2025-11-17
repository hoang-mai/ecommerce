package com.ecommerce.chat.entity;


import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "messages")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Message {

    @Id
    @Field("message_id")
    private String messageId;

    @Field("message_type")
    private MessageType messageType;

    @Field("message_content")
    private String messageContent;

    @Field("is_updated")
    private Boolean isUpdated;

    @Field("is_deleted")
    private Boolean isDeleted;

    @Field("is_read")
    private Boolean isRead;

    @Field("user_id")
    private Long userId;

    @Field("chat_id")
    private String chatId;

    @Field("created_at")
    @CreatedDate
    private LocalDateTime createdAt;

    @Field("updated_at")
    @LastModifiedDate
    private LocalDateTime updatedAt;

}