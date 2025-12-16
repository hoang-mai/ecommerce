package com.ecommerce.chat.notification.service.impl;

import com.ecommerce.chat.notification.dto.*;
import com.ecommerce.chat.notification.entity.*;
import com.ecommerce.chat.notification.repository.ChatRepository;
import com.ecommerce.chat.notification.repository.MessageRepository;
import com.ecommerce.chat.notification.repository.ShopCacheRepository;
import com.ecommerce.chat.notification.repository.UserCacheRepository;
import com.ecommerce.chat.notification.repository.impl.ChatRepositoryImpl;
import com.ecommerce.chat.notification.service.ChatService;
import com.ecommerce.chat.notification.service.FileService;
import com.ecommerce.chat.notification.service.PushSubscriptionService;
import com.ecommerce.library.component.UserHelper;
import com.ecommerce.library.enumeration.AccountStatus;
import com.ecommerce.library.enumeration.Role;
import com.ecommerce.library.exception.NotFoundException;
import com.ecommerce.library.kafka.event.shop.CreateShopEvent;
import com.ecommerce.library.kafka.event.shop.UpdateShopStatusEvent;
import com.ecommerce.library.kafka.event.user.UpdateUserEvent;
import com.ecommerce.library.utils.FnCommon;
import com.ecommerce.library.utils.MessageError;
import com.ecommerce.library.utils.PageResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.ecommerce.library.utils.Constant.TTL_SECONDS;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private static final Logger log = LoggerFactory.getLogger(ChatServiceImpl.class);

    private final MessageRepository messageRepository;
    private final ChatRepository chatRepository;
    private final ChatRepositoryImpl chatRepositoryImpl;
    private final UserCacheRepository userCacheRepository;
    private final ShopCacheRepository shopCacheRepository;
    private final UserHelper userHelper;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final FileService fileService;
    private final RedisTemplate<String, String> redisTemplate;
    private final PushSubscriptionService pushSubscriptionService;

    @Override
    public void createMessagePrivate(ReqPrivateMessageDTO reqPrivateMessageDTO, Principal principal) {
        String senderId = principal.getName();
        Long receiverId = reqPrivateMessageDTO.getReceiverId();

        Message message;
        if (FnCommon.isNotNullOrEmpty(reqPrivateMessageDTO.getChatId())) {
            Chat chat = chatRepository.findById(reqPrivateMessageDTO.getChatId())
                .orElseThrow(() -> new NotFoundException(MessageError.CHAT_NOT_FOUND));

            chat.getUserCacheList().forEach(userCache -> {
                if (userCache.getAccountStatus() != AccountStatus.ACTIVE) {
                    throw new NotFoundException(MessageError.ACCOUNT_DISABLED);
                }
            });

            if (FnCommon.isNotNull(chat.getShopCache()) &&
                chat.getShopCache().getShopStatus() != com.ecommerce.library.enumeration.ShopStatus.ACTIVE) {
                throw new NotFoundException(MessageError.SHOP_INACTIVE);
            }

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
            UserCache senderCache = chat.getUserCacheList().stream()
                .filter(userCache -> userCache.get_id().equals(senderId))
                .findFirst()
                .orElse(null);
            String senderName = senderCache != null ? senderCache.getFullName() : "Người dùng";
            Role receiverRole = Role.USER;

            // Kiểm tra xem có phải shop gửi cho user không
            if (FnCommon.isNotNull(chat.getShopCache()) &&
                chat.getShopCache().getOwnerId().equals(senderId)) {
                senderName = chat.getShopCache().getShopName();
            }

            // Kiểm tra xem receiver có phải shop owner không
            if (FnCommon.isNotNull(chat.getShopCache()) &&
                chat.getShopCache().getOwnerId().equals(String.valueOf(receiverId))) {
                receiverRole = Role.OWNER;
            }

            sendPushNotificationIfOffline(receiverId, senderName, reqPrivateMessageDTO.getMessageContent(), receiverRole);
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
            Chat chat = chatRepository.findById(reqPrivateMessageDTO.getChatId())
                .orElseThrow(() -> new NotFoundException(MessageError.CHAT_NOT_FOUND));

            chat.getUserCacheList().forEach(userCache -> {
                if (userCache.getAccountStatus() != AccountStatus.ACTIVE) {
                    throw new NotFoundException(MessageError.ACCOUNT_DISABLED);
                }
            });

            if (FnCommon.isNotNull(chat.getShopCache()) &&
                chat.getShopCache().getShopStatus() != com.ecommerce.library.enumeration.ShopStatus.ACTIVE) {
                throw new NotFoundException(MessageError.SHOP_INACTIVE);
            }

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

            UserCache senderCache = chat.getUserCacheList().stream()
                .filter(userCache -> userCache.get_id().equals(senderId))
                .findFirst()
                .orElse(null);
            String senderName = senderCache != null ? senderCache.getFullName() : "Người dùng";
            Role receiverRole = Role.USER;


            if (FnCommon.isNotNull(chat.getShopCache()) &&
                chat.getShopCache().getOwnerId().equals(senderId)) {
                senderName = chat.getShopCache().getShopName();
            }

            // Kiểm tra xem receiver có phải shop owner không
            if (FnCommon.isNotNull(chat.getShopCache()) &&
                chat.getShopCache().getOwnerId().equals(String.valueOf(receiverId))) {
                receiverRole = Role.OWNER;
            }

            String content = FnCommon.isNotNull(file) ? "Đã gửi một tệp tin" : reqPrivateMessageDTO.getMessageContent();
            sendPushNotificationIfOffline(receiverId, senderName, content, receiverRole);

            return chatRepository.save(chat);


        } else {
            UserCache senderCache = userCacheRepository.findById(senderId)
                .orElseThrow(() -> new NotFoundException(MessageError.USER_CACHE_NOT_FOUND));
            UserCache receiverCache = userCacheRepository.findById(String.valueOf(receiverId))
                .orElseThrow(() -> new NotFoundException(MessageError.USER_CACHE_NOT_FOUND));


            if (senderCache.getAccountStatus() != AccountStatus.ACTIVE) {
                throw new NotFoundException(MessageError.ACCOUNT_DISABLED);
            }
            if (receiverCache.getAccountStatus() != AccountStatus.ACTIVE) {
                throw new NotFoundException(MessageError.ACCOUNT_DISABLED);
            }

            Chat chat = Chat.builder()
                .build();

            chat.addUserCache(senderCache);
            chat.addUserCache(receiverCache);

            if (FnCommon.isNotNull(reqPrivateMessageDTO.getShopId())) {
                ShopCache shopCache = shopCacheRepository.findById(String.valueOf(reqPrivateMessageDTO.getShopId()))
                    .orElseThrow(() -> new NotFoundException(MessageError.SHOP_CACHE_NOT_FOUND));

                if (shopCache.getShopStatus() != com.ecommerce.library.enumeration.ShopStatus.ACTIVE) {
                    throw new NotFoundException(MessageError.SHOP_INACTIVE);
                }

                chat.setShopCache(shopCache);
                chat.setChatType(ChatType.CUSTOMER_SUPPORT);
            }

            chatRepository.save(chat);

            message = Message.builder()
                .messageType(reqPrivateMessageDTO.getMessageType())
                .senderId(senderId)
                .chatId(chat.get_id())
                .isDeleted(false)
                .isEdited(false)
                .receiverId(String.valueOf(receiverId))
                .readBy(List.of(senderId))
                .shopId(String.valueOf(reqPrivateMessageDTO.getShopId()))
                .build();
            messageRepository.save(message);
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

            String senderName = senderCache.getFullName();
            Role receiverRole = Role.USER;

            if (FnCommon.isNotNull(chat.getShopCache()) &&
                chat.getShopCache().getOwnerId().equals(senderId)) {
                senderName = chat.getShopCache().getShopName();
            }

            // Kiểm tra xem receiver có phải shop owner không
            if (FnCommon.isNotNull(chat.getShopCache()) &&
                chat.getShopCache().getOwnerId().equals(String.valueOf(receiverId))) {
                receiverRole = Role.OWNER;
            }

            String content = FnCommon.isNotNull(file) ? "Đã gửi một tệp tin" : reqPrivateMessageDTO.getMessageContent();
            sendPushNotificationIfOffline(receiverId, senderName, content, receiverRole);

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

    @Override
    public void updateAccountStatusInChats(Long userId, AccountStatus accountStatus) {
        chatRepositoryImpl.updateAccountStatusInChats(userId, accountStatus);
    }

    @Override
    public void updateShopStatusInChats(UpdateShopStatusEvent updateShopStatusEvent) {
        chatRepositoryImpl.updateShopStatusInChats(updateShopStatusEvent);
    }

    @Override
    public void heartbeat(StompHeaderAccessor accessor) {

        String sessionId = accessor.getSessionId();
        String userId = null;
        Principal principal = accessor.getUser();
        if (FnCommon.isNotNull(principal) ) {
            userId = principal.getName();
        } else {
            List<String> nativeUserIds = accessor.getNativeHeader("userId");
            if (nativeUserIds != null && !nativeUserIds.isEmpty()) {
                userId = nativeUserIds.get(0);
            }
        }

        if (!FnCommon.isNotNullOrEmpty(sessionId) || !FnCommon.isNotNullOrEmpty(userId)) {
            return;
        }

        try {
            String userKey = "chat:user:" + userId;
            String sessionKey = "chat:session:" + sessionId;

            redisTemplate.expire(sessionKey, TTL_SECONDS, TimeUnit.SECONDS);
            redisTemplate.expire(userKey, TTL_SECONDS, TimeUnit.SECONDS);

        } catch (Exception ignored) {
        }
    }

    /**
     * Kiểm tra user có online không bằng cách check key trong Redis
     */
    private boolean isUserOnline(String userId) {
        try {
            String userKey = "chat:user:" + userId;
            return redisTemplate.hasKey(userKey);
        } catch (Exception e) {
            log.warn("Failed to check online status for user: {}", userId, e);
            return false;
        }
    }

    /**
     * Gửi push notification cho user offline
     */
    private void sendPushNotificationIfOffline(Long userId, String senderName, String messageContent, Role role) {
        if (!isUserOnline(String.valueOf(userId))) {
            String url;
            if(Role.OWNER.equals(role)) {
                url = "/owner/chats";
            } else if (Role.ADMIN.equals(role)) {
                url = "/admin/chats";
            } else {
                url = "/";
            }
            try {
                NotificationDto notification = NotificationDto.builder()
                    .title("Tin nhắn mới từ " + senderName)
                    .message(messageContent)
                    .data(Map.of("notificationType", NotificationType.INFO,
                        "url",url
                        ))
                    .build();

                pushSubscriptionService.sendNotificationToUser(userId, notification);
                log.info("Sent push notification to offline user: {}", userId);
            } catch (Exception e) {
                log.error("Failed to send push notification to user: {}", userId, e);
            }
        }
    }


}
