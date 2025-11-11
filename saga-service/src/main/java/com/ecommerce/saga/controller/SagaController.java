package com.ecommerce.saga.controller;

import com.ecommerce.library.component.MessageService;
import com.ecommerce.library.utils.BaseResponse;
import com.ecommerce.library.utils.Constant;
import com.ecommerce.library.utils.MessageSuccess;
import com.ecommerce.saga.dto.ReqCreateUserDTO;
import com.ecommerce.saga.service.SagaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = Constant.SAGA)
@RequiredArgsConstructor
public class SagaController {
    private final SagaService sagaService;
    private final MessageService messageService;

    /**
     * Tạo người dùng mới
     *
     * @param reqCreateUserDTO Thông tin tạo người dùng mới
     * @return Trả về thành công
     */
    @PostMapping("/register")
    public ResponseEntity<BaseResponse<Void>> register(@Valid @RequestBody ReqCreateUserDTO reqCreateUserDTO) {
        sagaService.createUser(reqCreateUserDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(BaseResponse.<Void>builder()
                .statusCode(HttpStatus.CREATED.value())
                .message(messageService.getMessage(MessageSuccess.USER_CREATED_SUCCESS))
                .build());
    }

    /**
     * Chấp nhận yêu cầu làm chủ của hàng
     *
     * @param userVerificationId ID của yêu cầu xác minh người dùng
     * @return Trả về thành công
     */
    @PatchMapping("{userVerificationId}/approve")
    public ResponseEntity<BaseResponse<Void>> updateOwner(@PathVariable Long userVerificationId) {
        sagaService.approveOwnerRequest(userVerificationId);
        return ResponseEntity.status(HttpStatus.OK).body(BaseResponse.<Void>builder()
                .statusCode(HttpStatus.OK.value())
                .message(messageService.getMessage(MessageSuccess.UPDATE_OWNER_REQUEST_SUCCESS))
                .build());
    }

}
