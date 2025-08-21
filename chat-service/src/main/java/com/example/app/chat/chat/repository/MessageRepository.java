package com.example.app.chat.chat.repository;

import com.example.app.chat.chat.dto.ResMessageDTO;
import com.example.app.chat.chat.entity.Message;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends MongoRepository<Message, String> {

    @Aggregation(pipeline = {
            "{ '$match': { 'chatId': ?0 } }",
            """
            {
              $lookup: {
                from: 'chat_members',
                localField: 'sender_id',
                foreignField: 'chat_member_id',
                as: 'sender',
                pipeline: [
                  {
                    $lookup: {
                      from: 'users',
                      localField: 'user_id',
                      foreignField: 'user_id',
                      as: 'user',
                      pipeline: [
                        {
                          $project: {
                            userId: '$user_id',
                            avatarUrl: '$avatar_url'
                          }
                        }
                      ]
                    }
                  },
                  { $unwind: '$user' }
                ]
              }
            }
            """,
            "{ '$unwind': '$sender' }",
            """
                    {
                      $project: {
                        messageId: '$_id',
                        messageType: '$message_type',
                        content: {
                                  $cond: {
                                         if: { $eq: ['$lastMessage.is_deleted', true] },
                                         then: null,
                                         else: '$lastMessage.message_content'
                                         }
                                  },
                        timestamp: '$updated_at',
                        isUpdated: '$is_updated',
                        isDeleted: '$is_deleted',
                        chatMemberId: '$chat_member_id',
                        nickName: '$sender.nick_name',
                        sender: {
                          userId: '$sender.user.userId',
                          avatarUrl: '$sender.user.avatarUrl'
                        }
                      }
                    }
                    """
    })
    Slice<ResMessageDTO> findByChatId(String chatId, Pageable pageable);

}
