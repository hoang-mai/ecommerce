package com.ecommerce.chat.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "user_chats")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserChat {
    @Id
    @Field("user_chat_id")
    private String userChatId;

    @Field("user_id")
    private String userId;

    @Field("chat_id")
    private String chatId;

    @Field("is_read")
    private Boolean isRead;
}
