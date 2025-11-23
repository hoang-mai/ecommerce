package com.ecommerce.read.service;

import com.ecommerce.library.kafka.event.cart.CreateCartEvent;
import com.ecommerce.library.kafka.event.cart.UpdateProductCartItemEvent;
import com.ecommerce.library.kafka.event.cart.DeleteCartItemEvent;
import com.ecommerce.read.dto.CartViewDTO;

public interface CartViewService {
    void createCart(CreateCartEvent event);
    void updateCartItem(UpdateProductCartItemEvent event);
    void deleteCartItem(DeleteCartItemEvent event);

    CartViewDTO getCurrentUserCart();
}
