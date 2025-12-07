package com.ecommerce.chat.service;

import com.ecommerce.chat.dto.ReqUpdateMessageDTO;
import com.ecommerce.chat.entity.Message;
import com.ecommerce.library.utils.PageResponse;

public interface MessageChatService {
    /**
     * Cập nhật tin nhắn theo ID
     *
     * @param messageId ID của tin nhắn cần cập nhật
     * @param reqUpdateMessageDTO Thông tin cập nhật tin nhắn
     */
    void updateMessage(String messageId, ReqUpdateMessageDTO reqUpdateMessageDTO);

    PageResponse<Message> getMessages(String chatId, int pageNo, int pageSize);

    void markMessageAsRead(String chatId);

    Long countUnreadMessages(String chatId);
}
