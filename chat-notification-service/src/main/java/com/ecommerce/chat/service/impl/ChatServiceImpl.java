package com.ecommerce.chat.service.impl;

import com.ecommerce.chat.dto.*;
import com.ecommerce.chat.entity.*;
import com.ecommerce.chat.repository.*;
import com.ecommerce.chat.repository.impl.ChatRepositoryImpl;
import com.ecommerce.chat.service.ChatService;
import com.ecommerce.chat.service.FileService;
import com.ecommerce.library.component.UserHelper;
import com.ecommerce.library.exception.NotFoundException;
import com.ecommerce.library.kafka.event.shop.CreateShopEvent;
import com.ecommerce.library.kafka.event.user.UpdateUserEvent;
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
                .shopId(String.valueOf(reqPrivateMessageDTO.getShopId()))
                .build();
            messageRepository.save(message);
            Chat chat = chatRepository.findById(reqPrivateMessageDTO.getChatId())
                .orElseThrow(() -> new NotFoundException(MessageError.CHAT_NOT_FOUND));
            chat.setLastMessage(message);
            chatRepository.save(chat);
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
        }
    }


    @Override
    public PageResponse<Chat> getListChatPreview(int pageNo, int pageSize, String keyword, String shopId) {
        Long currentUserId = userHelper.getCurrentUserId();
        Sort sort = Sort.by(Sort.Direction.DESC, "updatedAt");
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
        Page<Chat> page = chatRepositoryImpl.findByUserId(currentUserId, pageable, keyword, shopId);
        return PageResponse.<Chat>builder()
            .data(page.getContent().stream().peek(chat -> {
                    chat.getUserCacheList().forEach(userCache -> userCache.setAvatarUrl(
                        fileService.getPresignedUrl(userCache.getAvatarUrl())));
                    if (chat.getShopCache() != null) {
                        chat.getShopCache().setLogoUrl(
                            fileService.getPresignedUrl(chat.getShopCache().getLogoUrl()));
                    }
                }
            ).toList())
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
        if(FnCommon.isNotNull(chat) ){
            chat.getUserCacheList().forEach(userCache -> userCache.setAvatarUrl(
                fileService.getPresignedUrl(userCache.getAvatarUrl())));
            if (FnCommon.isNotNull(chat.getShopCache())) {
                chat.getShopCache().setLogoUrl(
                    fileService.getPresignedUrl(chat.getShopCache().getLogoUrl()));
            }
        }

        return chat;
    }

    @Override
    public Chat uploadFileChatOrCreateChat(MultipartFile file, ReqPrivateMessageDTO reqPrivateMessageDTO) {
        String senderId = String.valueOf(userHelper.getCurrentUserId());
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
                .shopId(String.valueOf(reqPrivateMessageDTO.getShopId()))
                .build();
            messageRepository.save(message);
            Chat chat = chatRepository.findById(reqPrivateMessageDTO.getChatId())
                .orElseThrow(() -> new NotFoundException(MessageError.CHAT_NOT_FOUND));
            chat.setLastMessage(message);
            if(FnCommon.isNotNull(file)){
                String fileUrl = fileService.uploadFile(file, "chat/" + message.get_id());
                message.setMessageContent(fileUrl);
            }
            messageRepository.save(message);
            message.setMessageContent(
                FnCommon.isNotNull(file) ? fileService.getPresignedUrl(message.getMessageContent())
                    : reqPrivateMessageDTO.getMessageContent()
            );
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
            return chatRepository.save(chat);


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
                .shopId(String.valueOf(reqPrivateMessageDTO.getShopId()))
                .build();
            messageRepository.save(message);
            if (FnCommon.isNotNull(reqPrivateMessageDTO.getShopId())) {
                ShopCache shopCache = shopCacheRepository.findById(String.valueOf(reqPrivateMessageDTO.getShopId()))
                    .orElseThrow(() -> new NotFoundException(MessageError.SHOP_CACHE_NOT_FOUND));
                chat.setShopCache(shopCache);
                chat.setChatType(ChatType.CUSTOMER_SUPPORT);
            }
            chat.setLastMessage(message);
            if(FnCommon.isNotNull(file)){
                String fileUrl = fileService.uploadFile(file, "chat/" + message.get_id());
                message.setMessageContent(fileUrl);
            }else {
                message.setMessageContent(reqPrivateMessageDTO.getMessageContent());
            }
            simpMessagingTemplate.convertAndSendToUser(
                String.valueOf(reqPrivateMessageDTO.getReceiverId()),
                "/queue/messages",
                message
            );
            messageRepository.save(message);
            return chatRepository.save(chat);
        }


    }

    @Override
    public void updateAvatarInChats(Long userId, String avatarUrl) {
        chatRepositoryImpl.updateAvatarInChats(userId, avatarUrl);
    }

    @Override
    public void updateUserInChats(UpdateUserEvent updateUserEvent) {
        chatRepositoryImpl.updateUserInChats(updateUserEvent);
    }

    @Override
    public void updateShopInChats(CreateShopEvent createShopEvent) {
        chatRepositoryImpl.updateShopInChats(createShopEvent);
    }


}
