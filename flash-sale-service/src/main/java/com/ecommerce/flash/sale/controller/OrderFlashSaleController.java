package com.ecommerce.flash.sale.controller;

import com.ecommerce.flash.sale.dto.ResCreateOrderDTO;
import com.ecommerce.flash.sale.service.OrderFlashSaleService;
import com.ecommerce.library.component.MessageService;
import com.ecommerce.library.utils.BaseResponse;
import com.ecommerce.library.utils.Constant;
import com.ecommerce.library.utils.MessageSuccess;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Constant.ORDER_FLASH_SALE)
@RequiredArgsConstructor
public class OrderFlashSaleController {

    private final OrderFlashSaleService orderFlashSaleService;
    private final MessageService messageService;

    /**
     * Create a new order
     */
    @PostMapping
    @Operation(summary = "Create order", description = "Create a new order from cart items")
    public ResponseEntity<BaseResponse<Void>> createOrder(@Valid @RequestBody ResCreateOrderDTO request) {

        orderFlashSaleService.createOrder(request);

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(BaseResponse.<Void>builder()
                .statusCode(HttpStatus.CREATED.value())
                .message(messageService.getMessage(MessageSuccess.ORDER_CREATED_SUCCESS))
                .build());
    }

}
