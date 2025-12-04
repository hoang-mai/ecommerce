package com.ecommerce.chat.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Document(collection = "user_chats")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserChat extends BaseEntity {
    @Id
    @Field("_id")
    private String _id;

    @Field("userId")
    private String userId;

    @Field("shopId")
    private String shopId;

    @Field("chatId")
    private String chatId;

    @Field("unread_count")
    private Integer unreadCount; // Number of unread messages

    @Field("last_read_message_id")
    private String lastReadMessageId; // Last message ID that was read

    @Field("last_read_at")
    private LocalDateTime lastReadAt; // When the user last read messages

    @Field("is_muted")
    private Boolean isMuted; // Whether notifications are muted for this chat

    @Field("is_pinned")
    private Boolean isPinned; // Whether the chat is pinned to the top

    @Field("is_archived")
    private Boolean isArchived; // Whether the user has archived this chat

    @Field("is_blocked")
    private Boolean isBlocked; // Whether the user has blocked this chat

    @Field("role")
    private ChatRole role; // OWNER, ADMIN, MEMBER for group chats

    @Field("joined_at")
    private LocalDateTime joinedAt;

    @Field("left_at")
    private LocalDateTime leftAt;

    public enum ChatRole {
        OWNER,   // Creator of the chat
        ADMIN,   // Admin privileges
        MEMBER   // Regular member
    }
}
