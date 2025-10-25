package com.ecommerce.chat.service;

import com.ecommerce.chat.dto.*;
import com.example.app.chat.chat.dto.*;
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
     * @param userId ID của người dùng
     * @return Danh sách các cuộc trò chuyện
     */
    PageResponse<ResChatPreviewDTO> getListChatPreview(Long userId, int pageNo, int pageSize);

    /**
     * Lấy thông tin cuộc trò chuyện theo ID
     *
     * @param chatId   ID của cuộc trò chuyện
     * @param pageNo   Số trang
     * @param pageSize Kích thước trang
     * @return Thông tin cuộc trò chuyện
     */
    PageResponse<ResMessageDTO> getChatById(String chatId, int pageNo, int pageSize);


    /**
     * Tạo một tin nhắn nhóm
     *
     * @param reqPrivateMessageDTO Thông tin tin nhắn nhóm
     */
    void createMessageGroup(ReqPrivateMessageDTO reqPrivateMessageDTO);

    /**
     * Tạo một cuộc trò chuyện nhóm
     *
     * @param reqCreateGroupChatDTO Danh sách ID thành viên nhóm
     */
    void createGroupChat(ReqCreateGroupChatDTO reqCreateGroupChatDTO);

    /**
     * Thêm thành viên vào cuộc trò chuyện nhóm
     *
     * @param chatId                ID của cuộc trò chuyện nhóm
     * @param reqCreateGroupChatDTO Danh sách ID thành viên mới
     */
    void addMemberToGroupChat(String chatId, ReqCreateGroupChatDTO reqCreateGroupChatDTO);

    /**
     * Cập nhật thông tin cuộc trò chuyện
     *
     * @param chatId           ID của cuộc trò chuyện
     * @param reqUpdateChatDTO Thông tin cập nhật cuộc trò chuyện
     */
    void updateChat(String chatId, ReqUpdateChatDTO reqUpdateChatDTO);
}
