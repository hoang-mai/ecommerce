package com.ecommerce.chat.service;

import com.ecommerce.chat.dto.ReqUpdateMessageDTO;

public interface MessageChatService {
    /**
     * Cập nhật tin nhắn theo ID
     *
     * @param messageId ID của tin nhắn cần cập nhật
     * @param reqUpdateMessageDTO Thông tin cập nhật tin nhắn
     */
    void updateMessage(String messageId, ReqUpdateMessageDTO reqUpdateMessageDTO);
}
