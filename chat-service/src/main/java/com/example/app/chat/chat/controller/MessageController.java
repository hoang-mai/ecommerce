package com.example.app.chat.chat.controller;

import com.example.app.chat.chat.dto.ReqUpdateMessageDTO;
import com.example.app.chat.chat.service.MessageChatService;
import com.example.app.chat.library.component.MessageService;
import com.example.app.chat.library.utils.BaseResponse;
import com.example.app.chat.library.utils.Constant;
import com.example.app.chat.library.utils.MessageSuccess;
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
}
