package com.ecommerce.chat.service;

import com.ecommerce.chat.dto.ReqMarkAsReadDTO;

public interface UserChatService {
    void markChatAsRead(ReqMarkAsReadDTO reqMarkAsReadDTO);

    /**
     * Lấy số lượng userChat chưa đọc của người dùng hiện tại
     *
     * @return Số lượng userChat chưa đọc
     */
    long getUnreadCount();
}