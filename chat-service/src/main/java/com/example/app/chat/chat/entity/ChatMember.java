package com.example.app.chat.chat.entity;

import org.springframework.data.mongodb.core.mapping.Field;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ChatMember {

    @Field("user_id")
    private Long userId;

    @Field("nick_name")
    private String nickName;

    @Field("user_avatar_url")
    private String userAvatarUrl;

    @Field("user_name")
    private String userName;
}
