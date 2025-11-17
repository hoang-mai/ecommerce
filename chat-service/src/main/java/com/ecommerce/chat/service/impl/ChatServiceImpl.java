package com.ecommerce.chat.service.impl;

import com.ecommerce.chat.dto.*;
import com.ecommerce.chat.entity.Chat;
import com.ecommerce.chat.entity.Message;
import com.ecommerce.chat.entity.User;
import com.ecommerce.chat.repository.ChatRepository;
import com.ecommerce.chat.repository.MessageRepository;
import com.ecommerce.chat.repository.UserRepository;
import com.ecommerce.chat.service.ChatService;
import com.ecommerce.library.component.UserHelper;
import com.ecommerce.library.exception.HttpRequestException;
import com.ecommerce.library.exception.NotFoundException;
import com.ecommerce.library.utils.FnCommon;
import com.ecommerce.library.utils.MessageError;
import com.ecommerce.library.utils.PageResponse;
import com.ecommerce.user.UserServiceGrpc;
import com.ecommerce.utils.base_response.BaseResponse;
import com.google.protobuf.InvalidProtocolBufferException;
import io.grpc.Metadata;
import io.grpc.StatusRuntimeException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.grpc.client.interceptor.security.BearerTokenAuthenticationInterceptor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final MessageRepository messageRepository;
    private final ChatRepository chatRepository;
    private final UserRepository userRepository;
    private final UserHelper userHelper;

    @Transactional
    @Override
    public void createMessagePrivate(ReqPrivateMessageDTO reqPrivateMessageDTO) {
        Long senderId = userHelper.getCurrentUserId();

        if (FnCommon.isNotNullOrEmpty(reqPrivateMessageDTO.getChatId())) {
            Chat chat = Chat.builder()
                    .build();
            chatRepository.save(chat);

            Message message = Message.builder()
                    .messageType(reqPrivateMessageDTO.getMessageType())
                    .messageContent(reqPrivateMessageDTO.getMessageContent())
                    .userId(senderId)
                    .chatId(chat.getChatId())
                    .isDeleted(false)
                    .isUpdated(false)
                    .build();
            messageRepository.save(message);
        } else {
            if (chatRepository.existsByChatId(reqPrivateMessageDTO.getChatId())) {
                throw new NotFoundException(MessageError.CHAT_NOT_FOUND);
            }
            Message message = Message.builder()
                    .chatId(reqPrivateMessageDTO.getChatId())
                    .messageType(reqPrivateMessageDTO.getMessageType())
                    .messageContent(reqPrivateMessageDTO.getMessageContent())
                    .userId(senderId)
                    .isDeleted(false)
                    .isUpdated(false)
                    .build();
            messageRepository.save(message);
        }
    }


    @Override
    public PageResponse<ResChatPreviewDTO> getListChatPreview(Long userId, int pageNo, int pageSize) {
        Long currentUserId = userHelper.getCurrentUserId();
        if (!Objects.equals(currentUserId, userId)) {
            throw new NotFoundException(MessageError.USER_NOT_FOUND);
        }
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Slice<ResChatPreviewDTO> resChatPreviewDTOS = chatRepository.findByUserId(userId, pageable);
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
