package com.ecommerce.chat.service.impl;

import com.ecommerce.chat.dto.ReqUpdateMessageDTO;
import com.ecommerce.chat.entity.Message;
import com.ecommerce.chat.repository.MessageRepository;
import com.ecommerce.chat.repository.impl.MessageRepositoryImpl;
import com.ecommerce.chat.service.MessageChatService;
import com.ecommerce.library.component.UserHelper;
import com.ecommerce.library.exception.NotFoundException;
import com.ecommerce.library.utils.FnCommon;
import com.ecommerce.library.utils.MessageError;
import com.ecommerce.library.utils.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MessageChatServiceImpl implements MessageChatService {

    private final MessageRepository messageRepository;
    private final MessageRepositoryImpl messageRepositoryImpl;
    private final UserHelper userHelper;

    @Override
    public void updateMessage(String messageId, ReqUpdateMessageDTO reqUpdateMessageDTO) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new NotFoundException(MessageError.MESSAGE_NOT_FOUND));
        if (FnCommon.isNotNull(reqUpdateMessageDTO.getContent())) {
            message.setMessageContent(reqUpdateMessageDTO.getContent());
            message.setIsEdited(true);
        }
        if (FnCommon.isNotNull(reqUpdateMessageDTO.getIsDeleted())) {
            message.setIsDeleted(reqUpdateMessageDTO.getIsDeleted());
        }
        messageRepository.save(message);
    }

    @Override
    public PageResponse<Message> getMessages(String chatId, int pageNo, int pageSize) {
        Sort sort = Sort.by("createdAt").descending();
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
        Page<Message> page = messageRepositoryImpl.findByChatId(chatId, pageable);
        return PageResponse.<Message>builder()
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
    public void markMessageAsRead(String chatId) {
        Long userId = userHelper.getCurrentUserId();
        messageRepositoryImpl.markMessagesAsRead(chatId, String.valueOf(userId));
    }

    @Override
    public Long countUnreadMessages(String chatId) {
        Long userId = userHelper.getCurrentUserId();
        return messageRepositoryImpl.getCountUnreadMessages(chatId, String.valueOf(userId));
    }
}
