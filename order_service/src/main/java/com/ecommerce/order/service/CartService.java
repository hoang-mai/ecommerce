package com.ecommerce.order.service;

import com.ecommerce.order.dto.ReqAddToCartDTO;
import com.ecommerce.order.dto.ReqUpdateCartItemDTO;

public interface CartService {


    /**
     * Add item to cart
     */
    void addToCart(ReqAddToCartDTO request);

    /**
     * Update cart item quantity
     */
    void updateCartItem(Long cartItemId, ReqUpdateCartItemDTO request);

    /**
     * Remove item from cart
     */
    void removeCartItem( Long cartItemId);

    /**
     * Clear entire cart
     */
    void clearCart();

}

