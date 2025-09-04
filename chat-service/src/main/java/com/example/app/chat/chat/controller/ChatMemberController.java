package com.example.app.chat.chat.controller;

import com.example.app.chat.chat.dto.ReqUpdateChatMemberDTO;
import com.example.app.chat.chat.service.ChatMemberService;
import com.example.app.chat.library.component.MessageService;
import com.example.app.chat.library.utils.BaseResponse;
import com.example.app.chat.library.utils.Constant;
import com.example.app.chat.library.utils.MessageSuccess;
import jakarta.validation.Valid;
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
