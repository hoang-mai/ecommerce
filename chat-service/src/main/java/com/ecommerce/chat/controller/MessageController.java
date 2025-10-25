package com.ecommerce.chat.controller;

import com.ecommerce.chat.dto.ReqUpdateMessageDTO;
import com.ecommerce.chat.service.MessageChatService;
import com.ecommerce.library.component.MessageService;
import com.ecommerce.library.utils.BaseResponse;
import com.ecommerce.library.utils.Constant;
import com.ecommerce.library.utils.MessageSuccess;
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
