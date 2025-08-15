package com.example.app.chat.auth.controller;

import com.example.app.chat.library.component.MessageService;
import com.example.app.chat.auth.dto.auth.ReqLoginDTO;
import com.example.app.chat.auth.dto.auth.ResLoginDTO;
import com.example.app.chat.auth.service.AuthService;
import com.example.app.chat.library.utils.BaseResponse;
import com.example.app.chat.library.utils.Constant;
import com.example.app.chat.library.utils.MessageSuccess;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController()
@RequestMapping(Constant.AUTH)
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final MessageService messageService;

    @PostMapping("/login")
    public ResponseEntity<BaseResponse<ResLoginDTO>> login(@Valid @RequestBody ReqLoginDTO reqLoginDTO) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(BaseResponse
                        .<ResLoginDTO>builder()
                        .statusCode(HttpStatus.OK.value())
                        .message(messageService.getMessage(MessageSuccess.LOGIN_SUCCESS))
                        .data(authService.login(reqLoginDTO))
                        .build());
    }
}
