package com.example.app.chat.auth.controller;

import com.example.app.chat.auth.dto.auth.ReqUpdateAccountDTO;
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

    /**
     * Đăng nhập
     *
     * @param reqLoginDTO thông tin đăng nhập
     * @return Access Token và những thông tin khác của người dùng
     */
    @PostMapping("/login")
    public ResponseEntity<BaseResponse<ResLoginDTO>> login(@Valid @RequestBody ReqLoginDTO reqLoginDTO) {
        ResLoginDTO loginDTO= authService.login(reqLoginDTO);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(BaseResponse
                        .<ResLoginDTO>builder()
                        .statusCode(HttpStatus.OK.value())
                        .message(messageService.getMessage(MessageSuccess.LOGIN_SUCCESS))
                        .data(loginDTO)
                        .build());
    }

    /**
     * Cập nhật tài khoản
     *
     * @param reqUpdateAccountDTO thông tin cập nhật tài khoản
     * @return Trả về thành công
     */
    @PatchMapping()
    public ResponseEntity<BaseResponse<Void>> updateStatus(@Valid @RequestBody ReqUpdateAccountDTO reqUpdateAccountDTO) {
        authService.updateAccount(reqUpdateAccountDTO);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(BaseResponse
                        .<Void>builder()
                        .statusCode(HttpStatus.OK.value())
                        .message(messageService.getMessage(MessageSuccess.UPDATE_ACCOUNT_SUCCESS))
                        .build());
    }

}
