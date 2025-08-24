package com.example.app.chat.chat.repository;

import com.example.app.chat.chat.dto.ResChatPreviewDTO;
import com.example.app.chat.chat.entity.Chat;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRepository extends MongoRepository<Chat, String> {

    @Aggregation(pipeline = {
            """
                    { $lookup: {
                        from: "chat_members",
                        let: { chatId: "$chat_id" },
                        pipeline: [
                          { $match: { $expr: { $and: [
                                               { $eq: ["$chat_id", "$$chatId"] },
                                               { $eq: ["$user_id", ?0] }
                                               ] } } }
                        ],
                        as: "members"
                    } }
                    """,
            "{$unwind: {path: '$members', preserveNullAndEmptyArrays: true}}",
            """
                    {$lookup: {
                            from: 'messages',
                            let: { chatId: "$chat_id" },
                            as: 'lastMessage',
                            pipeline: [
                                        {$match: {$expr: { $eq: ["$chat_id", "$$chatId"] } } },
                                        {$lookup: {from: 'chat_members',
                                                   localField: 'sender_id',
                                                   foreignField: 'chat_member_id',
                                                   as: 'sender',
                                                   pipeline: [
                                                              {$project: {nick_name: 1}},
                                                              {$lookup: {
                                                                    from: 'users',
                                                                    localField: 'user_id',
                                                                    foreignField: 'user_id',
                                                                    as: 'user'
                                                                }},
                                                              {$unwind: {path: '$user', preserveNullAndEmptyArrays: true}}
                                                              ]}},
                                        {$unwind: {path: '$sender', preserveNullAndEmptyArrays: true}},
                                        {$sort: {updated_at: -1}},
                                        {$limit: 1}
                                      ]
                            }}
                    """,
            "{$unwind: {path: '$lastMessage', preserveNullAndEmptyArrays: true}}",
            """
                    {$project: {
                            chatId: '$chat_id',
                            chatName: '$chat_name',
                            chatImageUrl: '$chat_image_url',
                            lastMessage: {
                                        messageId: '$lastMessage.message_id',
                                        chatMemberId: '$lastMessage.sender_id',
                                        nickName: '$lastMessage.sender.nick_name',
                                        messageType: '$lastMessage.message_type',
                                        isDeleted: '$lastMessage.is_deleted',
                                        content: { $cond: {
                                            if: { $eq: ['$lastMessage.is_deleted', true] },
                                            then: null,
                                            else: '$lastMessage.message_content'
                                        }},
                                        timestamp: '$lastMessage.updated_at',
                                        sender: {
                                            senderId: '$lastMessage.sender.user.user_id',
                                            avatarUrl: '$lastMessage.sender.user.avatar_url',
                                        }}}}
                    """
    })
    Slice<ResChatPreviewDTO> findByUserId(Long userId, Pageable pageable);

    boolean existsByChatId(String chatId);
}
