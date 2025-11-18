package com.ecommerce.chat.controller;

import com.ecommerce.chat.dto.ReqMarkAsReadDTO;
import com.ecommerce.chat.service.UserChatService;
import com.ecommerce.library.component.MessageService;
import com.ecommerce.library.utils.BaseResponse;
import com.ecommerce.library.utils.Constant;
import com.ecommerce.library.utils.MessageSuccess;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = Constant.USER_CHAT)
@RequiredArgsConstructor
public class UserChatController {

    private final UserChatService userChatService;
    private final MessageService messageService;

    @PutMapping("/mark-as-read")
    public ResponseEntity<BaseResponse<Void>> markChatAsRead(
            @RequestBody ReqMarkAsReadDTO reqMarkAsReadDTO) {
        userChatService.markChatAsRead(reqMarkAsReadDTO);
        return ResponseEntity.ok(BaseResponse.<Void>builder()
                .statusCode(200)
                .message(messageService.getMessage(MessageSuccess.MARK_AS_READ_SUCCESS))
                .build()
        );
    }

    @GetMapping("/unread-count")
    public ResponseEntity<BaseResponse<Long>> getUnreadCount() {
        long unreadCount = userChatService.getUnreadCount();
        return ResponseEntity.ok(BaseResponse.<Long>builder()
                .statusCode(200)
                .message(messageService.getMessage(MessageSuccess.GET_UNREAD_COUNT_SUCCESS))
                .data(unreadCount)
                .build()
        );
    }

}
