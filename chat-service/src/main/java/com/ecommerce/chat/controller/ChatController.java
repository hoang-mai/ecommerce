package com.ecommerce.chat.controller;

import com.ecommerce.chat.dto.*;
import com.example.app.chat.chat.dto.*;
import com.ecommerce.chat.service.ChatService;
import com.ecommerce.library.component.MessageService;
import com.ecommerce.library.utils.BaseResponse;
import com.ecommerce.library.utils.Constant;
import com.ecommerce.library.utils.MessageSuccess;
import com.ecommerce.library.utils.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
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
    private final MessageService messageService;

    @MessageMapping("/private")
    public void sendPrivateMessage(@Payload ReqPrivateMessageDTO reqPrivateMessageDTO) {
        chatService.createMessagePrivate(reqPrivateMessageDTO);
        simpMessagingTemplate.convertAndSendToUser(
                reqPrivateMessageDTO.getReceiverId().toString(),
                "/queue/messages",
                reqPrivateMessageDTO
        );
    }

    @MessageMapping("/group/{groupId}")
    public void sendGroupMessage(@Payload ReqPrivateMessageDTO reqPrivateMessageDTO, @DestinationVariable String groupId) {
        chatService.createMessageGroup(reqPrivateMessageDTO);
        simpMessagingTemplate.convertAndSend(
                "/topic/group/" + groupId,
                reqPrivateMessageDTO
        );
    }

    @PostMapping()
    public ResponseEntity<BaseResponse<Void>> createGroupChat(@RequestBody ReqCreateGroupChatDTO reqCreateGroupChatDTO) {
        chatService.createGroupChat(reqCreateGroupChatDTO);
        return ResponseEntity.ok(BaseResponse.<Void>builder()
                .statusCode(200)
                .message(messageService.getMessage(MessageSuccess.CREATE_GROUP_CHAT_SUCCESS))
                .build()
        );
    }

    @PostMapping("/add-member/{chatId}")
    public ResponseEntity<BaseResponse<Void>> addMemberToGroupChat(
            @PathVariable String chatId,
            @RequestBody ReqCreateGroupChatDTO reqCreateGroupChatDTO) {
        chatService.addMemberToGroupChat(chatId, reqCreateGroupChatDTO);
        return ResponseEntity.ok(BaseResponse.<Void>builder()
                .statusCode(200)
                .message(messageService.getMessage(MessageSuccess.ADD_MEMBER_TO_GROUP_CHAT_SUCCESS))
                .build()
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
                .message(messageService.getMessage(MessageSuccess.GET_LIST_CHAT_PREVIEW_SUCCESS))
                .data(chatPreviews)
                .build()
        );
    }

    @GetMapping("/{chatId}")
    public ResponseEntity<BaseResponse<PageResponse<ResMessageDTO>>> getChatById(
            @PathVariable String chatId,
            @RequestParam(value = "pageNo", defaultValue = "0") int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        PageResponse<ResMessageDTO> chat = chatService.getChatById(chatId, pageNo, pageSize);
        return ResponseEntity.ok(BaseResponse.<PageResponse<ResMessageDTO>>builder()
                .statusCode(200)
                .message(messageService.getMessage(MessageSuccess.GET_CHAT_SUCCESS))
                .data(chat)
                .build()
        );
    }

    @PatchMapping("/{chatId}")
    public ResponseEntity<BaseResponse<Void>> updateChat(
            @PathVariable String chatId,
            @RequestBody ReqUpdateChatDTO reqUpdateChatDTO) {
        chatService.updateChat(chatId, reqUpdateChatDTO);
        return ResponseEntity.ok(BaseResponse.<Void>builder()
                .statusCode(200)
                .message(messageService.getMessage(MessageSuccess.UPDATE_CHAT_SUCCESS))
                .build()
        );
    }
}
