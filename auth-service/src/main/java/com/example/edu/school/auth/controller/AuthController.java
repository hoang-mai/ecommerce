package com.example.edu.school.auth.controller;

import com.example.edu.school.library.component.MessageService;
import com.example.edu.school.auth.dto.auth.ReqLoginDTO;
import com.example.edu.school.auth.dto.auth.ResLoginDTO;
import com.example.edu.school.auth.service.AuthService;
import com.example.edu.school.library.utils.BaseResponse;
import com.example.edu.school.library.utils.Constant;
import com.example.edu.school.library.utils.MessageSuccess;
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
