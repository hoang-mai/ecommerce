package com.ecommerce.chat.controller;

import com.ecommerce.chat.dto.ReqUpdateMessageDTO;
import com.ecommerce.chat.entity.Chat;
import com.ecommerce.chat.entity.Message;
import com.ecommerce.chat.service.MessageChatService;
import com.ecommerce.library.component.MessageService;
import com.ecommerce.library.utils.BaseResponse;
import com.ecommerce.library.utils.Constant;
import com.ecommerce.library.utils.MessageSuccess;
import com.ecommerce.library.utils.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping(Constant.MESSAGE)
public class MessageController {

    private final MessageChatService messageChatService;
    private final MessageService messageService;

    @PatchMapping("/{messageId}")
    public ResponseEntity<BaseResponse<ReqUpdateMessageDTO>> updateMessage(
            @PathVariable String messageId,
            @RequestBody ReqUpdateMessageDTO reqUpdateMessageDTO) {
        messageChatService.updateMessage(messageId, reqUpdateMessageDTO);
        return ResponseEntity.ok(BaseResponse.<ReqUpdateMessageDTO>builder()
                .statusCode(200)
                .message(messageService.getMessage(MessageSuccess.UPDATE_MESSAGE_SUCCESS))
                .data(reqUpdateMessageDTO)
                .build()
        );
    }


    @GetMapping()
    public ResponseEntity<BaseResponse<PageResponse<Message>>> getMessages(
        @RequestParam(value = "chatId") String chatId,
        @RequestParam(value = "pageNo", defaultValue = "0") int pageNo,
        @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        PageResponse<Message> chat = messageChatService.getMessages(chatId, pageNo, pageSize);
        return ResponseEntity.ok(BaseResponse.<PageResponse<Message>>builder()
            .statusCode(200)
            .message(messageService.getMessage(MessageSuccess.GET_CHAT_SUCCESS))
            .data(chat)
            .build()
        );
    }

    @PatchMapping("/isRead")
    public ResponseEntity<BaseResponse<String>> markMessageAsRead(
        @RequestParam(value = "chatId") String chatId) {
        messageChatService.markMessageAsRead(chatId);
        return ResponseEntity.ok(BaseResponse.<String>builder()
            .statusCode(200)
            .message(messageService.getMessage(MessageSuccess.MARK_MESSAGE_AS_READ_SUCCESS))
            .build()
        );
    }

    @GetMapping("/count-unread")
    public ResponseEntity<BaseResponse<Long>> countUnreadMessages(
        @RequestParam(value = "chatId", required = false) String chatId
    ) {
        Long count = messageChatService.countUnreadMessages(chatId);
        return ResponseEntity.ok(BaseResponse.<Long>builder()
            .statusCode(200)
            .message(messageService.getMessage(MessageSuccess.COUNT_UNREAD_MESSAGES_SUCCESS))
            .data(count)
            .build()
        );
    }
}
