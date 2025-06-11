package com.example.edu.school.chat.model;

import java.time.LocalDateTime;
import java.util.Set;

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

@Document(collection = "chats")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Chat {

    @Id
    @Field("chat_id")
    private String chatId;

    @CreatedDate
    @Field("created_at")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Field("updated_at")
    private LocalDateTime updatedAt;

    @Field("chat_members")
    private Set<ChatMember> chatMembers;

    @Field("chat_name")
    private String chatName;

    @Field("is_group_chat")
    private boolean isGroupChat;

    @Field("is_private_chat")
    private String chatImageUrl;

    @Field("admin_id")
    private Long adminId;

}