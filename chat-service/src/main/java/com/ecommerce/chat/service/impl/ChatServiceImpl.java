package com.ecommerce.chat.service.impl;

import com.ecommerce.chat.dto.*;
import com.ecommerce.chat.entity.*;
import com.ecommerce.chat.repository.*;
import com.ecommerce.chat.repository.impl.ChatRepositoryImpl;
import com.ecommerce.chat.service.ChatService;
import com.ecommerce.chat.service.FileService;
import com.ecommerce.library.component.UserHelper;
import com.ecommerce.library.exception.NotFoundException;
import com.ecommerce.library.utils.FnCommon;
import com.ecommerce.library.utils.MessageError;
import com.ecommerce.library.utils.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final MessageRepository messageRepository;
    private final ChatRepository chatRepository;
    private final ChatRepositoryImpl chatRepositoryImpl;
    private final UserCacheRepository userCacheRepository;
    private final ShopCacheRepository shopCacheRepository;
    private final UserHelper userHelper;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final FileService fileService;

    @Override
    public void createMessagePrivate(ReqPrivateMessageDTO reqPrivateMessageDTO, Principal principal) {
        String senderId = principal.getName();
        Long receiverId = reqPrivateMessageDTO.getReceiverId();

        Message message;
        if (FnCommon.isNotNullOrEmpty(reqPrivateMessageDTO.getChatId())) {
            message = Message.builder()
                .chatId(reqPrivateMessageDTO.getChatId())
                .messageType(reqPrivateMessageDTO.getMessageType())
                .messageContent(reqPrivateMessageDTO.getMessageContent())
                .senderId(String.valueOf(senderId))
                .isDeleted(false)
                .isEdited(false)
                .receiverId(String.valueOf(receiverId))
                .readBy(List.of(String.valueOf(senderId)))
                .build();
            messageRepository.save(message);
            Chat chat = chatRepository.findById(reqPrivateMessageDTO.getChatId())
                .orElseThrow(() -> new NotFoundException(MessageError.CHAT_NOT_FOUND));
            chat.setLastMessage(message);
            chatRepository.save(chat);

        } else {
            Chat chat = Chat.builder()
                .build();
            UserCache senderCache = userCacheRepository.findById(String.valueOf(senderId))
                .orElseThrow(() -> new NotFoundException(MessageError.USER_CACHE_NOT_FOUND));

            chat.addUserCache(senderCache);

            UserCache receiverCache = userCacheRepository.findById(String.valueOf(receiverId))
                .orElseThrow(() -> new NotFoundException(MessageError.USER_CACHE_NOT_FOUND));

            chat.addUserCache(receiverCache);
            chatRepository.save(chat);


            message = Message.builder()
                .messageType(reqPrivateMessageDTO.getMessageType())
                .messageContent(reqPrivateMessageDTO.getMessageContent())
                .senderId(String.valueOf(senderId))
                .chatId(chat.get_id())
                .isDeleted(false)
                .isEdited(false)
                .receiverId(String.valueOf(receiverId))
                .readBy(List.of(String.valueOf(senderId)))
                .build();
            messageRepository.save(message);
            if (FnCommon.isNotNull(reqPrivateMessageDTO.getShopId())) {
                ShopCache shopCache = shopCacheRepository.findById(String.valueOf(reqPrivateMessageDTO.getShopId()))
                    .orElseThrow(() -> new NotFoundException(MessageError.SHOP_CACHE_NOT_FOUND));
                chat.setShopCache(shopCache);
                chat.setChatType(ChatType.CUSTOMER_SUPPORT);
            }
            chat.setLastMessage(message);
            chatRepository.save(chat);
        }
        simpMessagingTemplate.convertAndSendToUser(
            String.valueOf(reqPrivateMessageDTO.getReceiverId()),
            "/queue/messages",
            message
        );
    }


    @Override
    public PageResponse<Chat> getListChatPreview(int pageNo, int pageSize, String keyword, String shopId) {
        Long currentUserId = userHelper.getCurrentUserId();
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<Chat> page = chatRepositoryImpl.findByUserId(currentUserId, pageable, keyword, shopId);
        return PageResponse.<Chat>builder()
            .data(page.getContent())
            .pageNo(page.getNumber())
            .pageSize(page.getSize())
            .totalElements(page.getTotalElements())
            .totalPages(page.getTotalPages())
            .hasNextPage(page.hasNext())
            .hasPreviousPage(page.hasPrevious())
            .build();
    }

    @Override
    public Chat getChatByIdOrShopId(String chatId, String shopId) {
        Chat chat = null;
        if (FnCommon.isNotNullOrEmpty(chatId)) {
            chat = chatRepository.findById(chatId)
                .orElse(null);
        } else if (FnCommon.isNotNullOrEmpty(shopId)) {
            Long currentUserId = userHelper.getCurrentUserId();
            chat = chatRepositoryImpl.findByShopId(shopId, String.valueOf(currentUserId));
        }
        return chat;
    }

    @Override
    public String uploadFileChat(MultipartFile file, ReqPrivateMessageDTO reqPrivateMessageDTO, Principal principal) {
        String senderId = principal.getName();
        Long receiverId = reqPrivateMessageDTO.getReceiverId();

        Message message;
        if (FnCommon.isNotNullOrEmpty(reqPrivateMessageDTO.getChatId())) {
            message = Message.builder()
                .chatId(reqPrivateMessageDTO.getChatId())
                .messageType(reqPrivateMessageDTO.getMessageType())
                .senderId(String.valueOf(senderId))
                .isDeleted(false)
                .isEdited(false)
                .receiverId(String.valueOf(receiverId))
                .readBy(List.of(String.valueOf(senderId)))
                .build();
            messageRepository.save(message);
            Chat chat = chatRepository.findById(reqPrivateMessageDTO.getChatId())
                .orElseThrow(() -> new NotFoundException(MessageError.CHAT_NOT_FOUND));
            chat.setLastMessage(message);
            chatRepository.save(chat);

        } else {
            Chat chat = Chat.builder()
                .build();
            UserCache senderCache = userCacheRepository.findById(String.valueOf(senderId))
                .orElseThrow(() -> new NotFoundException(MessageError.USER_CACHE_NOT_FOUND));

            chat.addUserCache(senderCache);

            UserCache receiverCache = userCacheRepository.findById(String.valueOf(receiverId))
                .orElseThrow(() -> new NotFoundException(MessageError.USER_CACHE_NOT_FOUND));

            chat.addUserCache(receiverCache);
            chatRepository.save(chat);


            message = Message.builder()
                .messageType(reqPrivateMessageDTO.getMessageType())
                .senderId(String.valueOf(senderId))
                .chatId(chat.get_id())
                .isDeleted(false)
                .isEdited(false)
                .receiverId(String.valueOf(receiverId))
                .readBy(List.of(String.valueOf(senderId)))
                .build();
            messageRepository.save(message);
            if (FnCommon.isNotNull(reqPrivateMessageDTO.getShopId())) {
                ShopCache shopCache = shopCacheRepository.findById(String.valueOf(reqPrivateMessageDTO.getShopId()))
                    .orElseThrow(() -> new NotFoundException(MessageError.SHOP_CACHE_NOT_FOUND));
                chat.setShopCache(shopCache);
                chat.setChatType(ChatType.CUSTOMER_SUPPORT);
            }
            chat.setLastMessage(message);
            chatRepository.save(chat);
        }
        String fileUrl = fileService.uploadFile(file, "chat/" + message.get_id());
        message.setMessageContent(fileUrl);
        messageRepository.save(message);
        simpMessagingTemplate.convertAndSendToUser(
            String.valueOf(reqPrivateMessageDTO.getReceiverId()),
            "/queue/messages",
            message
        );
        simpMessagingTemplate.convertAndSendToUser(
            String.valueOf(senderId),
            "/queue/messages",
            message
        );
        return fileUrl;
    }


}
