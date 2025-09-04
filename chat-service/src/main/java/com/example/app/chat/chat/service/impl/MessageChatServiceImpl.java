package com.example.app.chat.chat.service.impl;

import com.example.app.chat.chat.dto.ReqUpdateMessageDTO;
import com.example.app.chat.chat.entity.Message;
import com.example.app.chat.chat.repository.MessageRepository;
import com.example.app.chat.chat.service.MessageChatService;
import com.example.app.chat.library.exception.NotFoundException;
import com.example.app.chat.library.utils.FnCommon;
import com.example.app.chat.library.utils.MessageError;
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
            message.setUpdated(true);
        }
        if (FnCommon.isNotNull(reqUpdateMessageDTO.getIsDeleted())) {
            message.setDeleted(reqUpdateMessageDTO.getIsDeleted());
        }
        messageRepository.save(message);
    }
}
