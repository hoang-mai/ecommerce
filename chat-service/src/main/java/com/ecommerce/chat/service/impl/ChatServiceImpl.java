package com.ecommerce.chat.service.impl;

import com.ecommerce.chat.dto.*;
import com.ecommerce.chat.dto.*;
import com.ecommerce.chat.entity.Chat;
import com.ecommerce.chat.entity.Message;
import com.ecommerce.chat.entity.UserChat;
import com.ecommerce.chat.repository.ChatRepository;
import com.ecommerce.chat.repository.MessageRepository;
import com.ecommerce.chat.repository.UserChatRepository;
import com.ecommerce.chat.service.ChatService;
import com.ecommerce.library.component.UserHelper;
import com.ecommerce.library.exception.NotFoundException;
import com.ecommerce.library.utils.FnCommon;
import com.ecommerce.library.utils.MessageError;
import com.ecommerce.library.utils.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final MessageRepository messageRepository;
    private final ChatRepository chatRepository;
    private final UserChatRepository userChatRepository;
    private final UserHelper userHelper;

    @Transactional
    @Override
    public void createMessagePrivate(ReqPrivateMessageDTO reqPrivateMessageDTO) {
        Long senderId = userHelper.getCurrentUserId();
        Long receiverId = reqPrivateMessageDTO.getReceiverId();

        if (FnCommon.isNotNullOrEmpty(reqPrivateMessageDTO.getChatId())) {
            if (!chatRepository.existsById(reqPrivateMessageDTO.getChatId())) {
                throw new NotFoundException(MessageError.CHAT_NOT_FOUND);
            }
            UserChat userChat = userChatRepository.findUserChatByChatIdAndUserId(reqPrivateMessageDTO.getChatId(), String.valueOf(senderId))
                    .orElseThrow(() -> new NotFoundException(MessageError.USER_CHAT_NOT_FOUND));
            Message message = Message.builder()
                    .chatId(reqPrivateMessageDTO.getChatId())
                    .messageType(reqPrivateMessageDTO.getMessageType())
                    .messageContent(reqPrivateMessageDTO.getMessageContent())
                    .userChatId(userChat.getUserChatId())
                    .isDeleted(false)
                    .isUpdated(false)
                    .build();
            UserChat receiverUserChat = userChatRepository.findUserChatByChatIdAndUserIdNot(
                    reqPrivateMessageDTO.getChatId(),
                    String.valueOf(senderId)
            ).orElseThrow(() -> new NotFoundException(MessageError.USER_CHAT_NOT_FOUND));
            receiverUserChat.setIsRead(false);
            userChatRepository.save(receiverUserChat);
            messageRepository.save(message);

        } else {
            Chat chat = Chat.builder().build();
            chatRepository.save(chat);

            UserChat sender = UserChat.builder()
                    .chatId(chat.getChatId())
                    .userId(String.valueOf(senderId))
                    .isRead(true)
                    .build();


            UserChat receiver = UserChat.builder()
                    .chatId(chat.getChatId())
                    .userId(String.valueOf(receiverId))
                    .isRead(false)
                    .build();
            userChatRepository.saveAll(List.of(sender, receiver));

            Message message = Message.builder()
                    .messageType(reqPrivateMessageDTO.getMessageType())
                    .messageContent(reqPrivateMessageDTO.getMessageContent())
                    .userChatId(sender.getUserChatId())
                    .chatId(chat.getChatId())
                    .isDeleted(false)
                    .isUpdated(false)
                    .build();
            messageRepository.save(message);
        }
    }


    @Override
    public PageResponse<ResChatPreviewDTO> getListChatPreview(int pageNo, int pageSize) {
        Long currentUserId = userHelper.getCurrentUserId();
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Slice<ResChatPreviewDTO> resChatPreviewDTOS = chatRepository.findByUserId(currentUserId, pageable);
        return PageResponse.<ResChatPreviewDTO>builder()
                .pageNo(resChatPreviewDTOS.getNumber())
                .pageSize(resChatPreviewDTOS.getSize())
                .hasNextPage(resChatPreviewDTOS.hasNext())
                .hasPreviousPage(resChatPreviewDTOS.hasPrevious())
                .data(resChatPreviewDTOS.getContent())
                .build();
    }

    @Override
    public PageResponse<ResMessageDTO> getChatById(String chatId, int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Slice<ResMessageDTO> resMessageDTOS = messageRepository.findByChatId(chatId, pageable);
        return PageResponse.<ResMessageDTO>builder()
                .pageNo(resMessageDTOS.getNumber())
                .pageSize(resMessageDTOS.getSize())
                .hasNextPage(resMessageDTOS.hasNext())
                .hasPreviousPage(resMessageDTOS.hasPrevious())
                .data(resMessageDTOS.getContent())
                .build();
    }


}
