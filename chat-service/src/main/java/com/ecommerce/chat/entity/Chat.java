package com.ecommerce.chat.entity;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "chats")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Chat extends BaseEntity {

    @Id
    @JsonProperty("chatId")
    private String _id;

    @Field("chatType")
    private ChatType chatType;

    @Field("shopCache")
    private ShopCache shopCache;

    @Field("userCacheList")
    @Builder.Default
    private List<UserCache> userCacheList = new ArrayList<>();

    @Field("lastMessage")
    private Message lastMessage;

    public void addUserCache(UserCache userCache) {
        this.userCacheList.add(userCache);
    }

}

