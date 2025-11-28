package com.ecommerce.read.controller;

import com.ecommerce.library.component.MessageService;
import com.ecommerce.library.utils.BaseResponse;
import com.ecommerce.library.utils.Constant;
import com.ecommerce.library.utils.MessageSuccess;
import com.ecommerce.read.dto.CartViewDTO;
import com.ecommerce.read.service.CartViewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = Constant.CART_VIEW)
@RequiredArgsConstructor
public class CartViewController {
    private final CartViewService cartViewService;
    private final MessageService messageService;

    /**
     * Xem giỏ hàng của người dùng hiện tại
     */
    @GetMapping()
    public ResponseEntity<BaseResponse<CartViewDTO>> getCurrentUserCart() {
        CartViewDTO cartView = cartViewService.getCurrentUserCart();
        return ResponseEntity.ok(BaseResponse.<CartViewDTO>builder()
                .statusCode(HttpStatus.OK.value())
                .message(messageService.getMessage(MessageSuccess.CART_RETRIEVED_SUCCESS))
                .data(cartView)
                .build());
    }
}
