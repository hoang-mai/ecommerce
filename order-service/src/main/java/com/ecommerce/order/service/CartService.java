package com.ecommerce.order.service;

import com.ecommerce.order.dto.ReqAddToCartDTO;
import com.ecommerce.order.dto.ReqUpdateCartItemDTO;

import java.util.List;

public interface CartService {


    /**
     * Add item to cart
     */
    void addToCart(ReqAddToCartDTO request);

    /**
     * Update cart item quantity
     */
    void updateCartItem(Long productCartItemId, ReqUpdateCartItemDTO request);

    /**
     * Remove item from cart
     */
    void removeProductCartItem( Long productCartItemId);

    /**
     * Clear entire cart
     */
    void clearCart();

    /**
     * Get cart item count by userId
     */
    Integer getCartItemCount();

    void clearCartItems(List<Long> cartItemIds);
}

