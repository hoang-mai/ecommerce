package com.ecommerce.order.service;

import com.ecommerce.order.dto.AddToCartRequest;
import com.ecommerce.order.dto.CartDTO;
import com.ecommerce.order.dto.UpdateCartItemRequest;

public interface CartService {


    /**
     * Add item to cart
     */
    CartDTO addToCart(Long userId, AddToCartRequest request);

    /**
     * Update cart item quantity
     */
    CartDTO updateCartItem(Long userId, Long cartItemId, UpdateCartItemRequest request);

    /**
     * Remove item from cart
     */
    void removeCartItem(Long userId, Long cartItemId);

    /**
     * Clear entire cart
     */
    void clearCart(Long userId);

    CartDTO getCart();
}

