package com.ecommerce.chat.service;

import com.ecommerce.chat.dto.*;
import com.ecommerce.library.utils.PageResponse;

public interface ChatService {

    /**
     * Tạo một tin nhắn riêng tư
     *
     * @param reqPrivateMessageDTO Thông tin tin nhắn riêng tư
     */
    void createMessagePrivate(ReqPrivateMessageDTO reqPrivateMessageDTO);

    /**
     * Lấy danh sách các cuộc trò chuyện của người dùng
     *
     * @param pageNo   Số trang
     * @param pageSize Kích thước trang
     * @return Danh sách các cuộc trò chuyện
     */
    PageResponse<ResChatPreviewDTO> getListChatPreview(int pageNo, int pageSize);

    /**
     * Lấy thông tin cuộc trò chuyện theo ID
     *
     * @param chatId   ID của cuộc trò chuyện
     * @param pageNo   Số trang
     * @param pageSize Kích thước trang
     * @return Thông tin cuộc trò chuyện
     */
    PageResponse<ResMessageDTO> getChatById(String chatId, int pageNo, int pageSize);

}
