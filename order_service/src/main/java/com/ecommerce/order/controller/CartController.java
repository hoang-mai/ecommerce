package com.ecommerce.order.controller;

import com.ecommerce.library.component.MessageService;
import com.ecommerce.library.utils.BaseResponse;
import com.ecommerce.library.utils.Constant;
import com.ecommerce.library.utils.MessageSuccess;
import com.ecommerce.order.dto.ReqAddToCartDTO;
import com.ecommerce.order.dto.ReqUpdateCartItemDTO;
import com.ecommerce.order.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = Constant.CART)
@RequiredArgsConstructor
@Tag(name = "Cart Management", description = "APIs for managing shopping cart")
public class CartController {

    private final CartService cartService;
    private final MessageService messageService;

    /**
     * Thêm sản phẩm vào giỏ hàng
     */
    @PostMapping("")
    @Operation(summary = "Add to cart", description = "Add item to shopping cart")
    public ResponseEntity<BaseResponse<Void>> addToCart(@Valid @RequestBody ReqAddToCartDTO request) {

        cartService.addToCart(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(BaseResponse.<Void>builder()
                        .statusCode(HttpStatus.CREATED.value())
                        .message(messageService.getMessage(MessageSuccess.ADD_TO_CART_SUCCESS))
                        .build());
    }

    /**
     * Update cart item quantity
     */
    @PatchMapping("/{cartItemId}")
    @Operation(summary = "Update cart item", description = "Update cart item quantity")
    public ResponseEntity<BaseResponse<Void>> updateCartItem(
            @PathVariable Long cartItemId,
            @Valid @RequestBody ReqUpdateCartItemDTO request) {
        cartService.updateCartItem(cartItemId, request);

        return ResponseEntity.ok(BaseResponse.<Void>builder()
                .statusCode(HttpStatus.OK.value())
                .message(messageService.getMessage(MessageSuccess.UPDATE_CART_ITEM_SUCCESS))
                .build());
    }

    /**
     * Remove item from cart
     */
    @DeleteMapping("/{cartItemId}")
    @Operation(summary = "Remove cart item", description = "Remove item from shopping cart")
    public ResponseEntity<BaseResponse<Void>> removeCartItem(
            @PathVariable Long cartItemId) {
        cartService.removeCartItem(cartItemId);

        return ResponseEntity.ok(BaseResponse.<Void>builder()
                .statusCode(HttpStatus.OK.value())
                .message(messageService.getMessage(MessageSuccess.REMOVE_CART_ITEM_SUCCESS))
                .build());
    }

    /**
     * Clear cart
     */
    @DeleteMapping
    @Operation(summary = "Clear cart", description = "Remove all items from shopping cart")
    public ResponseEntity<BaseResponse<Void>> clearCart() {
        cartService.clearCart();

        return ResponseEntity.ok(BaseResponse.<Void>builder()
                .statusCode(HttpStatus.OK.value())
                .message(messageService.getMessage(MessageSuccess.CLEAR_CART_SUCCESS))
                .build());
    }
}

