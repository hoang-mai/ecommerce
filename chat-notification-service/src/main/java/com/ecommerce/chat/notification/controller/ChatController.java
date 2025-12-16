package com.ecommerce.chat.notification.controller;

import com.ecommerce.chat.notification.dto.*;
import com.ecommerce.chat.notification.dto.ReqPrivateMessageDTO;
import com.ecommerce.chat.notification.entity.Chat;
import com.ecommerce.chat.notification.service.ChatService;
import com.ecommerce.library.component.MessageService;
import com.ecommerce.library.utils.BaseResponse;
import com.ecommerce.library.utils.Constant;
import com.ecommerce.library.utils.MessageSuccess;
import com.ecommerce.library.utils.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;

@RequiredArgsConstructor
@RestController
@RequestMapping(Constant.CHAT)
public class ChatController {

    private final ChatService chatService;
    private final MessageService messageService;

    @MessageMapping("/private")
    public void sendPrivateMessage(@Payload ReqPrivateMessageDTO reqPrivateMessageDTO, Principal principal) {
        chatService.createMessagePrivate(reqPrivateMessageDTO,principal);

    }

    @MessageMapping("/heartbeat")
    public void heartbeat( StompHeaderAccessor stompHeaderAccessor) {
        chatService.heartbeat(stompHeaderAccessor);
    }

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<BaseResponse<Chat>> uploadFileChatOrCreateChat(
        @RequestPart(value = "file", required = false)MultipartFile file,
        @RequestPart("data") ReqPrivateMessageDTO reqPrivateMessageDTO) {
        Chat chat = chatService.uploadFileChatOrCreateChat(file, reqPrivateMessageDTO);
        return ResponseEntity.ok(BaseResponse.<Chat>builder()
            .statusCode(200)
            .message(messageService.getMessage(MessageSuccess.UPLOAD_FILE_CHAT_SUCCESS))
            .data(chat)
            .build()
        );
    }

    @GetMapping()
    public ResponseEntity<BaseResponse<PageResponse<Chat>>> getListChatPreview(
        @RequestParam(value = "keyword", required = false) String keyword,
        @RequestParam(value = "shopId", required = false) String shopId,
        @RequestParam(value = "pageNo", defaultValue = "0") int pageNo,
        @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        PageResponse<Chat> chatPreviews = chatService.getListChatPreview(pageNo, pageSize, keyword, shopId);
        return ResponseEntity.ok(BaseResponse.<PageResponse<Chat>>builder()
            .statusCode(200)
            .message(messageService.getMessage(MessageSuccess.GET_LIST_CHAT_PREVIEW_SUCCESS))
            .data(chatPreviews)
            .build()
        );
    }

    @GetMapping("/chat")
    public ResponseEntity<BaseResponse<Chat>> getChatById(
        @RequestParam(value = "chatId", required =false) String chatId,
        @RequestParam(value = "shopId", required =false) String shopId
        ) {
        Chat chat = chatService.getChatByIdOrShopId(chatId, shopId);
        return ResponseEntity.ok(BaseResponse.<Chat>builder()
            .statusCode(200)
            .message(messageService.getMessage(MessageSuccess.GET_CHAT_SUCCESS))
            .data(chat)
            .build()
        );
    }


}
