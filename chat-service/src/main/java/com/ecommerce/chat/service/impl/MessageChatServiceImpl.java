package com.ecommerce.chat.service.impl;

import com.ecommerce.chat.dto.ReqUpdateMessageDTO;
import com.ecommerce.chat.entity.Message;
import com.ecommerce.chat.repository.MessageRepository;
import com.ecommerce.chat.service.MessageChatService;
import com.ecommerce.library.exception.NotFoundException;
import com.ecommerce.library.utils.FnCommon;
import com.ecommerce.library.utils.MessageError;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MessageChatServiceImpl implements MessageChatService {

    private final MessageRepository messageRepository;

    @Override
    public void updateMessage(String messageId, ReqUpdateMessageDTO reqUpdateMessageDTO) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new NotFoundException(MessageError.MESSAGE_NOT_FOUND));
        if (FnCommon.isNotNull(reqUpdateMessageDTO.getContent())) {
            message.setMessageContent(reqUpdateMessageDTO.getContent());
            message.setIsUpdated(true);
        }
        if (FnCommon.isNotNull(reqUpdateMessageDTO.getIsDeleted())) {
            message.setIsDeleted(reqUpdateMessageDTO.getIsDeleted());
        }
        messageRepository.save(message);
    }
}
