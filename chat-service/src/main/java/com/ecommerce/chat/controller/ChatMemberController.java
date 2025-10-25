package com.ecommerce.chat.controller;

import com.ecommerce.chat.dto.ReqUpdateChatMemberDTO;
import com.ecommerce.chat.service.ChatMemberService;
import com.ecommerce.library.component.MessageService;
import com.ecommerce.library.utils.BaseResponse;
import com.ecommerce.library.utils.Constant;
import com.ecommerce.library.utils.MessageSuccess;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping(Constant.CHAT_MEMBER)
public class ChatMemberController {

    private final MessageService messageService;
    private final ChatMemberService chatMemberService;

    @PatchMapping("/{chatMemberId}")
    public ResponseEntity<BaseResponse<Void>> updateChatMember(@RequestBody ReqUpdateChatMemberDTO reqUpdateChatMemberDTO, @PathVariable String chatMemberId) {
        chatMemberService.updateChatMember(chatMemberId, reqUpdateChatMemberDTO);
        return ResponseEntity.ok(BaseResponse.<Void>builder()
                .statusCode(200)
                .message(messageService.getMessage(MessageSuccess.UPDATE_NICKNAME_SUCCESS))
                .build()
        );
    }

}
