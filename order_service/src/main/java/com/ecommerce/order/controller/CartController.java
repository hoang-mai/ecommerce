package com.ecommerce.order.controller;

import com.ecommerce.library.component.MessageService;
import com.ecommerce.library.utils.BaseResponse;
import com.ecommerce.library.utils.Constant;
import com.ecommerce.library.utils.MessageSuccess;
import com.ecommerce.order.dto.AddToCartRequest;
import com.ecommerce.order.dto.CartDTO;
import com.ecommerce.order.dto.UpdateCartItemRequest;
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
     * Get current user's cart
     */
    @GetMapping
    @Operation(summary = "Get cart", description = "Get current user's shopping cart")
    public ResponseEntity<BaseResponse<CartDTO>> getCart() {

        CartDTO cart = cartService.getCart();

        return ResponseEntity.ok(BaseResponse.<CartDTO>builder()
                .statusCode(HttpStatus.OK.value())
                .message(messageService.getMessage(MessageSuccess.GET_CART_SUCCESS))
                .data(cart)
                .build());
    }

    /**
     * Add item to cart
     */
    @PostMapping("/items")
    @Operation(summary = "Add to cart", description = "Add item to shopping cart")
    public ResponseEntity<BaseResponse<CartDTO>> addToCart(
            ,
            @Valid @RequestBody AddToCartRequest request) {

        Long userId = Long.valueOf(authentication.getName());
        CartDTO cart = cartService.addToCart(userId, request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(BaseResponse.<CartDTO>builder()
                        .statusCode(HttpStatus.CREATED.value())
                        .message("Item added to cart successfully")
                        .data(cart)
                        .build());
    }

    /**
     * Update cart item quantity
     */
    @PatchMapping("/items/{cartItemId}")
    @Operation(summary = "Update cart item", description = "Update cart item quantity")
    public ResponseEntity<BaseResponse<CartDTO>> updateCartItem(
            ,
            @PathVariable Long cartItemId,
            @Valid @RequestBody UpdateCartItemRequest request) {

        Long userId = Long.valueOf(authentication.getName());
        CartDTO cart = cartService.updateCartItem(userId, cartItemId, request);

        return ResponseEntity.ok(BaseResponse.<CartDTO>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Cart item updated successfully")
                .data(cart)
                .build());
    }

    /**
     * Remove item from cart
     */
    @DeleteMapping("/items/{cartItemId}")
    @Operation(summary = "Remove cart item", description = "Remove item from shopping cart")
    public ResponseEntity<BaseResponse<Void>> removeCartItem(
            ,
            @PathVariable Long cartItemId) {

        Long userId = Long.valueOf(authentication.getName());
        cartService.removeCartItem(userId, cartItemId);

        return ResponseEntity.ok(BaseResponse.<Void>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Cart item removed successfully")
                .build());
    }

    /**
     * Clear cart
     */
    @DeleteMapping
    @Operation(summary = "Clear cart", description = "Remove all items from shopping cart")
    public ResponseEntity<BaseResponse<Void>> clearCart() {
        Long userId = Long.valueOf(authentication.getName());
        cartService.clearCart(userId);

        return ResponseEntity.ok(BaseResponse.<Void>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Cart cleared successfully")
                .build());
    }
}

