package com.ecommerce.order.service.impl;

import com.ecommerce.library.component.UserHelper;
import com.ecommerce.library.exception.NotFoundException;
import com.ecommerce.library.kafka.event.cart.*;
import com.ecommerce.library.utils.FnCommon;
import com.ecommerce.library.utils.MessageError;
import com.ecommerce.order.dto.ReqAddToCartDTO;
import com.ecommerce.order.dto.ReqUpdateCartItemDTO;
import com.ecommerce.order.entity.*;
import com.ecommerce.order.messaging.producer.CartEventProducer;
import com.ecommerce.order.repository.*;
import com.ecommerce.order.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;


@Service
@RequiredArgsConstructor
@Transactional
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserHelper userHelper;
    private final ProductCartItemRepository productCartItemRepository;
    private final ProductCacheRepository productCacheRepository;
    private final ShopCacheRepository shopCacheRepository;
    private final CartEventProducer cartEventProducer;

    @Override
    public void addToCart(ReqAddToCartDTO request) {

        Long userId = userHelper.getCurrentUserId();

        if(!shopCacheRepository.existsById(request.getShopId())){
            throw new NotFoundException(MessageError.SHOP_NOT_FOUND);
        }
        ProductCache productCache = productCacheRepository.findById(request.getProductId())
                .orElseThrow(() -> new NotFoundException(MessageError.PRODUCT_NOT_FOUND));

        if(!productCache.getProductVariantIds().contains(request.getProductVariantId())) {
            throw new NotFoundException(MessageError.PRODUCT_VARIANT_NOT_FOUND);
        }

        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> createNewCart(userId));

        CartItem existingItem = cart.getCartItems().stream()
                .filter(item -> item.getShopId().equals(request.getShopId()))
                .findFirst()
                .orElse(null);

        if (FnCommon.isNotNull(existingItem)) {

            ProductCartItem productCartItem = existingItem.getProductCartItems().stream()
                    .filter(pci -> pci.getProductVariantId().equals(request.getProductVariantId()))
                    .findFirst()
                    .orElse(null);
            if (FnCommon.isNotNull(productCartItem)) {
                productCartItem.setQuantity(productCartItem.getQuantity() + request.getQuantity());
            } else {
                ProductCartItem newProductCartItem = ProductCartItem.builder()
                        .productId(request.getProductId())
                        .productVariantId(request.getProductVariantId())
                        .quantity(request.getQuantity())
                        .build();
                existingItem.addProductCartItem(newProductCartItem);
            }
            cartItemRepository.save(existingItem);
        } else {
            CartItem newItem = CartItem.builder()
                    .cart(cart)
                    .shopId(request.getShopId())
                    .build();

            ProductCartItem productCartItem = ProductCartItem.builder()
                    .productId(request.getProductId())
                    .productVariantId(request.getProductVariantId())
                    .quantity(request.getQuantity())
                    .build();
            newItem.addProductCartItem(productCartItem);
            cart.addCartItem(newItem);
        }
        cartRepository.save(cart);
        cartEventProducer.send(
                CreateCartEvent.builder()
                        .cartId(cart.getCartId())
                        .userId(cart.getUserId())
                        .createdAt(cart.getCreatedAt())
                        .updatedAt(cart.getUpdatedAt())
                        .createCartItemEventList(
                                cart.getCartItems().stream().map(cartItem -> CreateCartItemEvent.builder()
                                        .cartItemId(cartItem.getCartItemId())
                                        .shopId(cartItem.getShopId())
                                        .createProductCartItemEvents(cartItem.getProductCartItems().stream().map(productCartItem -> CreateProductCartItemEvent.builder()
                                                .productCartItemId(productCartItem.getProductCartItemId())
                                                .productId(productCartItem.getProductId())
                                                .productVariantId(productCartItem.getProductVariantId())
                                                .quantity(productCartItem.getQuantity())
                                                .build()).toList())
                                        .build()).toList()
                        )
                        .build()
        );
    }

    @Override
    public void updateCartItem( Long productCartItemId, ReqUpdateCartItemDTO request) {
        ProductCartItem productCartItem = productCartItemRepository.findById(productCartItemId)
                .orElseThrow(() -> new NotFoundException(MessageError.CART_ITEM_NOT_FOUND));
        productCartItem.setQuantity(request.getQuantity());
        productCartItemRepository.save(productCartItem);
        cartEventProducer.send(
                UpdateProductCartItemEvent.builder()
                        .cartId(productCartItem.getCartItem().getCart().getCartId())
                        .cartItemId(productCartItem.getCartItem().getCartItemId())
                        .productCartItemId(productCartItem.getProductCartItemId())
                        .quantity(request.getQuantity())
                        .build()
        );
    }

    @Override
    public void removeCartItem(Long cartItemId) {

        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new NotFoundException(MessageError.CART_ITEM_NOT_FOUND));
        cartItemRepository.delete(cartItem);
        cartEventProducer.send(
                DeleteCartItemEvent.builder()
                        .cartId(cartItem.getCart().getCartId())
                        .cartItemId(cartItem.getCartItemId())
                        .build()
        );
    }

    @Override
    public void clearCart() {
        Long userId = userHelper.getCurrentUserId();
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException(MessageError.CART_NOT_FOUND));

        cart.clearCart();
        cartRepository.save(cart);
        cartEventProducer.send(
                DeleteCartItemEvent.builder()
                        .cartId(cart.getCartId())
                        .isDeletedAllItems(true)
                        .build()
        );

    }

    @Override
    public Integer getCartItemCount() {
        Long userId = userHelper.getCurrentUserId();
        return cartRepository.countCartItemsByUserId(userId);
    }

    @Override
    public void clearCartByUserId(Long userId) {
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

