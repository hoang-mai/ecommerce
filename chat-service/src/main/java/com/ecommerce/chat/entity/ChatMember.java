package com.ecommerce.chat.entity;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Document(collection = "chat_members")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ChatMember {

    @Id
    @Field("chat_member_id")
    private String chatMemberId;

    @Field("chat_id")
    private String chatId;

    @Field("user_id")
    private Long userId;

    @Field("nick_name")
    private String nickName;

    @Field("is_admin")
    private Boolean isAdmin;

    @Field("is_deleted")
    private Boolean isDeleted;

    @Field("created_at")
    @CreatedDate
    private LocalDateTime createdAt;

    @Field("updated_at")
    @LastModifiedDate
    private LocalDateTime updatedAt;
}
