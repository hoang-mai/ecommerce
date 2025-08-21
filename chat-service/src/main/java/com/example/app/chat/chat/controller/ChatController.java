package com.example.app.chat.chat.controller;

import com.example.app.chat.chat.dto.ReqPrivateMessageDTO;
import com.example.app.chat.chat.dto.ResChatPreviewDTO;
import com.example.app.chat.chat.dto.ResMessageDTO;
import com.example.app.chat.chat.service.ChatService;
import com.example.app.chat.library.utils.BaseResponse;
import com.example.app.chat.library.utils.Constant;
import com.example.app.chat.library.utils.MessageSuccess;
import com.example.app.chat.library.utils.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping(Constant.CHAT)
public class ChatController {

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final ChatService chatService;

    @MessageMapping("/private")
    public void sendPrivateMessage(@Payload ReqPrivateMessageDTO reqPrivateMessageDTO) {
        chatService.createMessagePrivate(reqPrivateMessageDTO);
        simpMessagingTemplate.convertAndSendToUser(
                reqPrivateMessageDTO.getReceiverId().toString(),
                "/queue/messages",
                reqPrivateMessageDTO
        );
    }

    @GetMapping("/list")
    public ResponseEntity<BaseResponse<PageResponse<ResChatPreviewDTO>>> getListChatPreview(
            @RequestHeader("user-id") Long userId,
            @RequestParam(value = "pageNo", defaultValue = "0") int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        PageResponse<ResChatPreviewDTO> chatPreviews = chatService.getListChatPreview(userId, pageNo, pageSize);
        return ResponseEntity.ok(BaseResponse.<PageResponse<ResChatPreviewDTO>>builder()
                .statusCode(200)
                .message(MessageSuccess.GET_LIST_CHAT_PREVIEW_SUCCESS)
                .data(chatPreviews)
                .build()
        );
    }
    @GetMapping("{chatId}")
    public ResponseEntity<BaseResponse<PageResponse<ResMessageDTO>>> getChatById(
            @PathVariable String chatId,
            @RequestParam(value = "pageNo", defaultValue = "0") int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        PageResponse<ResMessageDTO> chat = chatService.getChatById(chatId, pageNo, pageSize);
        return ResponseEntity.ok(BaseResponse.<PageResponse<ResMessageDTO>>builder()
                .statusCode(200)
                .message(MessageSuccess.GET_CHAT_SUCCESS)
                .data(chat)
                .build()
        );
    }
}
