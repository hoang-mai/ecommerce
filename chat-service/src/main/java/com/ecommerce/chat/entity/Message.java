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
public class Message extends BaseEntity{

    @Id
    @Field("_id")
    private String _id;

    @Field("messageType")
    private MessageType messageType;

    @Field("messageContent")
    private String messageContent;

    @Field("isUpdated")
    private Boolean isUpdated;

    @Field("isDeleted")
    private Boolean isDeleted;

    @Field("userChatId")
    private String userChatId;

    @Field("chatId")
    private String chatId;

}