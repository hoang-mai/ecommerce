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
    @Field("_id")
    private String _id;

    @Field("userId")
    private String userId;

    @Field("shopId")
    private String shopId;

    @Field("chatId")
    private String chatId;

    @Field("isRead")
    private Boolean isRead;
}
