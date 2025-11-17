package com.ecommerce.order.service.impl;

import com.ecommerce.library.component.UserHelper;
import com.ecommerce.library.exception.NotFoundException;
import com.ecommerce.library.utils.MessageError;
import com.ecommerce.order.dto.ReqAddToCartDTO;
import com.ecommerce.order.dto.ReqUpdateCartItemDTO;
import com.ecommerce.order.entity.Cart;
import com.ecommerce.order.entity.CartItem;
import com.ecommerce.order.entity.ProductCartItem;
import com.ecommerce.order.repository.CartItemRepository;
import com.ecommerce.order.repository.CartRepository;
import com.ecommerce.order.repository.ProductCartItemRepository;
import com.ecommerce.order.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Transactional
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserHelper userHelper;
    private final ProductCartItemRepository productCartItemRepository;

    @Override
    public void addToCart(ReqAddToCartDTO request) {

        Long userId = userHelper.getCurrentUserId();

        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> createNewCart(userId));

        // Check if item already exists in cart
        CartItem existingItem = cart.getCartItems().stream()
                .filter(item -> item.getProductId().equals(request.getProductId()))
                .findFirst()
                .orElse(null);

        if (existingItem != null) {
            // Update quantity if item exists
            ProductCartItem productCartItem = existingItem.getProductCartItems().stream()
                    .filter(pci -> pci.getProductVariantId().equals(request.getProductVariantId()))
                    .findFirst()
                    .orElse(null);
            if (productCartItem != null) {
                productCartItem.setQuantity(productCartItem.getQuantity() + request.getQuantity());
            } else {
                ProductCartItem newProductCartItem = ProductCartItem.builder()
                        .productVariantId(request.getProductVariantId())
                        .quantity(request.getQuantity())
                        .build();
                existingItem.addProductCartItem(newProductCartItem);
            }
            cartItemRepository.save(existingItem);
        } else {
            CartItem newItem = CartItem.builder()
                    .cart(cart)
                    .productId(request.getProductId())
                    .build();

            ProductCartItem productCartItem = ProductCartItem.builder()
                    .productVariantId(request.getProductVariantId())
                    .quantity(request.getQuantity())
                    .build();
            newItem.addProductCartItem(productCartItem);
            cart.addCartItem(newItem);
        }
        cartRepository.save(cart);

    }

    @Override
    public void updateCartItem( Long productCartItemId, ReqUpdateCartItemDTO request) {
        ProductCartItem productCartItem = productCartItemRepository.findById(productCartItemId)
                .orElseThrow(() -> new NotFoundException(MessageError.CART_ITEM_NOT_FOUND));
        productCartItem.setQuantity(request.getQuantity());
        productCartItemRepository.save(productCartItem);
    }

    @Override
    public void removeCartItem(Long cartItemId) {

        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new NotFoundException(MessageError.CART_ITEM_NOT_FOUND));
        cartItemRepository.delete(cartItem);
    }

    @Override
    public void clearCart() {
        Long userId = userHelper.getCurrentUserId();
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException(MessageError.CART_NOT_FOUND));

        cart.clearCart();
        cartRepository.save(cart);

    }

    private Cart createNewCart(Long userId) {
        Cart cart = Cart.builder()
                .userId(userId)
                .build();
        return cartRepository.save(cart);
    }
}

