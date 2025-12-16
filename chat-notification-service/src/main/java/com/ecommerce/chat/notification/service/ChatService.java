package com.ecommerce.chat.notification.service;

import com.ecommerce.chat.notification.dto.ReqPrivateMessageDTO;
import com.ecommerce.chat.notification.entity.Chat;
import com.ecommerce.library.enumeration.AccountStatus;
import com.ecommerce.library.kafka.event.shop.CreateShopEvent;
import com.ecommerce.library.kafka.event.shop.UpdateShopStatusEvent;
import com.ecommerce.library.kafka.event.user.UpdateUserEvent;
import com.ecommerce.library.utils.PageResponse;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;

import java.security.Principal;

public interface ChatService {

    /**
     * Tạo một tin nhắn riêng tư
     *
     * @param reqPrivateMessageDTO Thông tin tin nhắn riêng tư
     */
    void createMessagePrivate(ReqPrivateMessageDTO reqPrivateMessageDTO, Principal principal);

    /**
     * Lấy danh sách các cuộc trò chuyện của người dùng
     *
     * @param keyword  Từ khóa tìm kiếm (optional)
     * @param pageNo   Số trang
     * @param pageSize Kích thước trang
     * @return Danh sách các cuộc trò chuyện
     */
    PageResponse<Chat> getListChatPreview(int pageNo, int pageSize, String keyword, String shopId);

    Chat getChatByIdOrShopId(String chatId, String shopId);

    Chat uploadFileChatOrCreateChat(MultipartFile file, ReqPrivateMessageDTO reqPrivateMessageDTO);

    void updateAvatarInChats(Long userId, String avatarUrl);

    void updateUserInChats(UpdateUserEvent updateUserEvent);

    void updateShopInChats(CreateShopEvent createShopEvent);

    void updateAccountStatusInChats(Long userId, AccountStatus accountStatus);

    void updateShopStatusInChats(UpdateShopStatusEvent updateShopStatusEvent);

    void heartbeat(StompHeaderAccessor stompHeaderAccessor);
}
