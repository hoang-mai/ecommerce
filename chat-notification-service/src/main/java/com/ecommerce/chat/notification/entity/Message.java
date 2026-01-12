package com.ecommerce.chat.notification.entity;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
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
    @JsonProperty("messageId")
    private String _id;

    @Field("chatId")
    private String chatId;

    @Field("senderId")
    private String senderId;

    @Field("shopId")
    private String shopId;

    @Field("receiverId")
    private String receiverId;

    @Field("messageType")
    private MessageType messageType;

    @Field("messageContent")
    private String messageContent;

    @Field("replyToMessageId")
    private String replyToMessageId;

    @Field("isEdited")
    private Boolean isEdited;

    @Field("isDeleted")
    private Boolean isDeleted;

    @Field("deletedAt")
    private Instant deletedAt;

    @Field("readBy")
    @Builder.Default
    private List<String> readBy = new ArrayList<>();

}

