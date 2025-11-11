package com.ecommerce.auth.controller;

import com.ecommerce.auth.dto.auth.*;
import com.ecommerce.library.component.MessageService;
import com.ecommerce.auth.service.AuthService;
import com.ecommerce.library.utils.BaseResponse;
import com.ecommerce.library.utils.Constant;
import com.ecommerce.library.utils.MessageSuccess;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
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
        ResLoginDTO loginDTO = authService.login(reqLoginDTO);

        ResponseCookie accessTokenCookie = ResponseCookie.from("accessToken", loginDTO.getAccessToken())
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(loginDTO.getExpiresIn())
                .sameSite("Lax")
                .build();


        return ResponseEntity
                .status(HttpStatus.OK)
                .header("Set-Cookie", accessTokenCookie.toString())
                .body(BaseResponse
                        .<ResLoginDTO>builder()
                        .statusCode(HttpStatus.OK.value())
                        .message(messageService.getMessage(MessageSuccess.LOGIN_SUCCESS))
                        .data(loginDTO)
                        .build());
    }

    /**
     * Đăng xuất
     *
     * @return Trả về thành công và xóa cookies
     */
    @PostMapping("/logout")
    public ResponseEntity<BaseResponse<Void>> logout() {
        ResponseCookie deleteAccessTokenCookie = ResponseCookie.from("accessToken", "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0)
                .sameSite("Lax")
                .build();
        authService.logout();
        return ResponseEntity
                .status(HttpStatus.OK)
                .header("Set-Cookie", deleteAccessTokenCookie.toString())
                .body(BaseResponse
                        .<Void>builder()
                        .statusCode(HttpStatus.OK.value())
                        .message(messageService.getMessage(MessageSuccess.LOGOUT_SUCCESS))
                        .build());
    }

    /**
     * Cập nhật tài khoản
     *
     * @param reqUpdateAccountDTO thông tin cập nhật tài khoản
     * @return Trả về thành công
     */
    @PatchMapping()
    public ResponseEntity<BaseResponse<Void>> updateAccount(@Valid @RequestBody ReqUpdateAccountDTO reqUpdateAccountDTO) {
        authService.updateAccount(reqUpdateAccountDTO);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(BaseResponse
                        .<Void>builder()
                        .statusCode(HttpStatus.OK.value())
                        .message(messageService.getMessage(MessageSuccess.UPDATE_ACCOUNT_SUCCESS))
                        .build());
    }

    /**
     * Admin cập nhật trạng thái tài khoản
     *
     * @param reqUpdateAccountDTO thông tin cập nhật tài khoản
     * @param userId              ID của người dùng
     * @return Trả về thành công
     */
    @PatchMapping("{userId}")
    public ResponseEntity<BaseResponse<Void>> adminUpdateAccountStatus(
            @RequestBody ReqUpdateAccountDTO reqUpdateAccountDTO,
            @PathVariable Long userId) {
        authService.adminUpdateAccountStatus(reqUpdateAccountDTO, userId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(BaseResponse
                        .<Void>builder()
                        .statusCode(HttpStatus.OK.value())
                        .message(messageService.getMessage(MessageSuccess.UPDATE_ACCOUNT_SUCCESS))
                        .build());
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<BaseResponse<ResRefreshTokenDTO>> refreshToken(@Valid @RequestBody ReqRefreshTokenDTO reqRefreshTokenDTO) {
        ResRefreshTokenDTO resRefreshTokenDTO = authService.refreshToken(reqRefreshTokenDTO);

        ResponseCookie accessTokenCookie = ResponseCookie.from("accessToken", resRefreshTokenDTO.getAccessToken())
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(resRefreshTokenDTO.getExpiresIn())
                .sameSite("Lax")
                .build();

        return ResponseEntity
                .status(HttpStatus.OK)
                .header("Set-Cookie", accessTokenCookie.toString())
                .body(BaseResponse
                        .<ResRefreshTokenDTO>builder()
                        .statusCode(HttpStatus.OK.value())
                        .message(messageService.getMessage(MessageSuccess.REFRESH_TOKEN_SUCCESS))
                        .data(resRefreshTokenDTO)
                        .build());
    }

}
