package com.ecommerce.chat.entity;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "chats")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Chat extends BaseEntity {

    @Id
    @Field("_id")
    @JsonProperty("chatId")
    private String _id;

    @Field("chatType")
    private ChatType chatType;

    @Field("shopId")
    private String shopId;

    @Field("participantIds")
    private List<String> participantIds;

    @Field("lastMessageId")
    private String lastMessageId;

    @Field("lastMessageTime")
    private LocalDateTime lastMessageTime;

}

