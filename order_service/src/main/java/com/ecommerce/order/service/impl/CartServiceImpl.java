package com.ecommerce.order.service.impl;

import com.ecommerce.library.component.UserHelper;
import com.ecommerce.order.dto.AddToCartRequest;
import com.ecommerce.order.dto.CartDTO;
import com.ecommerce.order.dto.UpdateCartItemRequest;
import com.ecommerce.order.entity.Cart;
import com.ecommerce.order.entity.CartItem;
import com.ecommerce.order.mapper.OrderMapper;
import com.ecommerce.order.repository.CartItemRepository;
import com.ecommerce.order.repository.CartRepository;
import com.ecommerce.order.service.CartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderMapper orderMapper;
    private final UserHelper userHelper;


    @Override
    public CartDTO addToCart(Long userId, AddToCartRequest request) {
        // Get or create cart
        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> createNewCart(userId));

        // Check if item already exists in cart
        CartItem existingItem = cart.getCartItems().stream()
                .filter(item -> item.getProductVariantId().equals(request.getProductVariantId()))
                .findFirst()
                .orElse(null);

        if (existingItem != null) {
            // Update quantity if item exists
            existingItem.setQuantity(existingItem.getQuantity() + request.getQuantity());
            cartItemRepository.save(existingItem);
        } else {
            // Add new item to cart
            CartItem newItem = CartItem.builder()
                    .cart(cart)
                    .productId(request.getProductId())
                    .productVariantId(request.getProductVariantId())
                    .quantity(request.getQuantity())
                    .price(BigDecimal.ZERO) // Price should be fetched from product service
                    .shopId(request.getShopId())
                    .build();

            cart.addCartItem(newItem);
            cartItemRepository.save(newItem);
        }

        cart = cartRepository.save(cart);
        log.info("Added item to cart for user: {}, product variant: {}", userId, request.getProductVariantId());

        return orderMapper.toCartDTO(cart);
    }

    @Override
    public CartDTO updateCartItem(Long userId, Long cartItemId, UpdateCartItemRequest request) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Cart not found for user: " + userId));

        CartItem cartItem = cart.getCartItems().stream()
                .filter(item -> item.getCartItemId().equals(cartItemId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Cart item not found: " + cartItemId));

        cartItem.setQuantity(request.getQuantity());
        cartItemRepository.save(cartItem);

        log.info("Updated cart item: {} for user: {}", cartItemId, userId);

        return orderMapper.toCartDTO(cart);
    }

    @Override
    public void removeCartItem(Long userId, Long cartItemId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Cart not found for user: " + userId));

        CartItem cartItem = cart.getCartItems().stream()
                .filter(item -> item.getCartItemId().equals(cartItemId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Cart item not found: " + cartItemId));

        cart.removeCartItem(cartItem);
        cartItemRepository.delete(cartItem);
        cartRepository.save(cart);

        log.info("Removed cart item: {} for user: {}", cartItemId, userId);
    }

    @Override
    public void clearCart(Long userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Cart not found for user: " + userId));

        cart.clearCart();
        cartRepository.save(cart);

        log.info("Cleared cart for user: {}", userId);
    }

    @Override
    public CartDTO getCart() {
        Long userId = userHelper.getCurrentUserId();
        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> createNewCart(userId));

        return orderMapper.toCartDTO(cart);
    }

    private Cart createNewCart(Long userId) {
        Cart cart = Cart.builder()
                .userId(userId)
                .build();
        return cartRepository.save(cart);
    }
}

