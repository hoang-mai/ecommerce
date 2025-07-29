package com.example.edu.school.auth.controller;

import com.example.edu.school.library.component.MessageService;
import com.example.edu.school.auth.dto.account.ReqCreateAccountDTO;
import com.example.edu.school.auth.service.AccountService;
import com.example.edu.school.library.utils.BaseResponse;
import com.example.edu.school.library.utils.Constant;
import com.example.edu.school.library.utils.MessageSuccess;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController()
@RequestMapping(Constant.ACCOUNT)
@RequiredArgsConstructor
public class AccountController {

    private final MessageService messageService;
    private final AccountService accountService;

    @PostMapping("/create")
    public ResponseEntity<BaseResponse<Void>> createAccount(@Valid @RequestBody ReqCreateAccountDTO reqCreateAccountDTO) {
        accountService.createAccount(reqCreateAccountDTO);
        return  ResponseEntity
                .status(HttpStatus.CREATED)
                .body(BaseResponse
                        .<Void>builder()
                        .statusCode(HttpStatus.CREATED.value())
                        .message(messageService.getMessage(MessageSuccess.ACCOUNT_CREATED_SUCCESS))
                        .build());
    }
    @GetMapping("/test")
    public ResponseEntity<BaseResponse<String>> test() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(BaseResponse
                        .<String>builder()
                        .statusCode(HttpStatus.OK.value())
                        .message("messageService.getMessage(MessageSuccess.TEST_SUCCESS)")
                        .data("Test successful")
                        .build());
    }
}
