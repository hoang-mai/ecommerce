package com.ecommerce.chat.entity;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

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
    @Field("_id")
    @JsonProperty("messageId")
    private String _id;

    @Field("chat_id")
    private String chatId;

    @Field("senderId")
    private String senderId;

    @Field("senderName")
    private String senderName;

    @Field("senderAvatarUrl")
    private String senderAvatarUrl;

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
    private LocalDateTime deletedAt;

    @Field("readBy")
    private List<String> readBy;

}

