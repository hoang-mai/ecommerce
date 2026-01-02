package com.ecommerce.read.service.impl;

import com.ecommerce.library.component.UserHelper;
import com.ecommerce.library.exception.NotFoundException;
import com.ecommerce.library.kafka.event.cart.CreateCartEvent;
import com.ecommerce.library.kafka.event.cart.UpdateProductCartItemEvent;
import com.ecommerce.library.kafka.event.cart.DeleteCartItemEvent;
import com.ecommerce.library.kafka.event.cart.DeleteProductCartItemEvent;
import com.ecommerce.library.utils.FnCommon;
import com.ecommerce.library.utils.MessageError;
import com.ecommerce.read.dto.CartViewDTO;
import com.ecommerce.read.dto.UserCategoryDTO;
import com.ecommerce.read.entity.CartView;
import com.ecommerce.read.repository.CartViewRepository;
import com.ecommerce.read.repository.impl.CartViewRepositoryImpl;
import com.ecommerce.read.service.CartViewService;
import com.ecommerce.read.service.FileService;
import com.ecommerce.read.service.ProductViewService;
import com.ecommerce.read.service.UserCategoryService;
import com.ecommerce.library.enumeration.UserCategoryType;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import java.util.concurrent.CompletableFuture;


@Service
@RequiredArgsConstructor
public class CartViewServiceImpl implements CartViewService {

    private final UserHelper userHelper;
    private final CartViewRepository cartViewRepository;
    private final CartViewRepositoryImpl cartViewRepositoryImpl;
    private final FileService fileService;
    private final ProductViewService productViewService;
    private final UserCategoryService userCategoryService;

    @Override
    public void createCart(CreateCartEvent event) {
        CartView cartView = CartView.builder()
            ._id(String.valueOf(event.getCartId()))
            .userId(String.valueOf(event.getUserId()))
            .createdAt(event.getCreatedAt())
            .updatedAt(event.getUpdatedAt())
            .cartItems(
                event.getCreateCartItemEventList().stream().map(
                    itemEvent -> CartView.CartItem.builder()
                        ._id(String.valueOf(itemEvent.getCartItemId()))
                        .shopId(String.valueOf(itemEvent.getShopId()))
                        .productCartItems(itemEvent.getCreateProductCartItemEvents().stream().map(
                            productCartItemEvent -> CartView.ProductCartItem.builder()
                                ._id(String.valueOf(productCartItemEvent.getProductCartItemId()))
                                .productId(String.valueOf(productCartItemEvent.getProductId()))
                                .productVariantId(String.valueOf(productCartItemEvent.getProductVariantId()))
                                .quantity(productCartItemEvent.getQuantity())
                                .build()
                        ).toList())
                        .build()
                ).toList()
            )
            .build();
        cartViewRepository.save(cartView);
        CompletableFuture.runAsync(() -> event.getCreateCartItemEventList().forEach(cartItemEvent -> cartItemEvent.getCreateProductCartItemEvents().forEach(productCartItemEvent -> {
            String categoryIdStr = productViewService.getProductById(productCartItemEvent.getProductId(), false).getCategoryId();
            if (FnCommon.isNotNullOrEmpty(categoryIdStr)) {
                UserCategoryDTO userCategoryDTO = UserCategoryDTO.builder()
                    .categoryId(Long.parseLong(categoryIdStr))
                    .userCategoryType(UserCategoryType.ADD_TO_CART)
                    .build();
                userCategoryService.addUserCategoryByUserId(event.getUserId(), userCategoryDTO);
            }
        })));
    }

    @Override
    public void updateCartItem(UpdateProductCartItemEvent event) {
        cartViewRepositoryImpl.updateCartItem(event);
    }

    @Override
    public void deleteCartItem(DeleteCartItemEvent event) {

        if (FnCommon.isNotNull(event.getIsDeletedAllItems()) && event.getIsDeletedAllItems()) {
            CartView cartView = cartViewRepository.findById(String.valueOf(event.getCartId()))
                .orElseThrow(() -> new NotFoundException(MessageError.CART_NOT_FOUND));

            cartView.getCartItems().forEach(cartItem ->
                cartItem.getProductCartItems().forEach(productCartItem -> {
                    subUserCategoryScore(cartView, productCartItem);

                })
            );
            cartView.clearCart();
            cartViewRepository.save(cartView);
            return;
        }

        CartView cartView = cartViewRepository.findById(String.valueOf(event.getCartId())).orElse(null);
        if (FnCommon.isNotNull(cartView)) {
            CartView.CartItem cartItemToRemove = cartView.getCartItems().stream()
                .filter(item -> item.get_id().equals(String.valueOf(event.getCartItemId())))
                .findFirst()
                .orElse(null);
            if (FnCommon.isNotNull(cartItemToRemove)) {
                cartItemToRemove.getProductCartItems().forEach(productCartItem -> {
                    subUserCategoryScore(cartView, productCartItem);

                    cartView.removeCartItem(cartItemToRemove);
                });
                cartViewRepository.save(cartView);
            }
        }
    }

    @Override
    public void deleteProductCartItem(DeleteProductCartItemEvent event) {
        CartView cartView = cartViewRepository.findById(String.valueOf(event.getCartId()))
            .orElseThrow(() -> new NotFoundException(MessageError.CART_NOT_FOUND));

        CartView.CartItem cartItem = cartView.getCartItems().stream()
            .filter(item -> item.get_id().equals(String.valueOf(event.getCartItemId())))
            .findFirst()
            .orElse(null);

        if (FnCommon.isNotNull(cartItem)) {
            if (event.getIsDeleteCartItem()) {

                cartItem.getProductCartItems().forEach(productCartItem -> {

                    subUserCategoryScore(cartView, productCartItem);
                });


                cartView.removeCartItem(cartItem);
            } else {
                CartView.ProductCartItem productCartItemToRemove = cartItem.getProductCartItems().stream()
                    .filter(pci -> pci.get_id().equals(String.valueOf(event.getProductCartItemId())))
                    .findFirst()
                    .orElse(null);

                if (FnCommon.isNotNull(productCartItemToRemove)) {
                    subUserCategoryScore(cartView, productCartItemToRemove);


                    cartItem.getProductCartItems().remove(productCartItemToRemove);
                }
            }
            cartViewRepository.save(cartView);
        }
    }

    private void subUserCategoryScore(CartView cartView, CartView.ProductCartItem productCartItemToRemove) {
        Long productId = Long.parseLong(productCartItemToRemove.getProductId());
        try {
            String categoryIdStr = productViewService.getProductById(productId, false).getCategoryId();
            if (FnCommon.isNotNullOrEmpty(categoryIdStr)) {
                UserCategoryDTO userCategoryDTO = UserCategoryDTO.builder()
                    .categoryId(Long.parseLong(categoryIdStr))
                    .userCategoryType(UserCategoryType.REMOVE_FROM_CART)
                    .build();
                userCategoryService.addUserCategoryByUserId(Long.valueOf(cartView.getUserId()), userCategoryDTO);
            }
        } catch (Exception ignored) {
        }
    }

    @Override
    public CartViewDTO getCurrentUserCart() {
        CartViewDTO cartViewDTO = cartViewRepositoryImpl.findCartViewDTOByUserId(String.valueOf(userHelper.getCurrentUserId()));
        if (FnCommon.isNotNull(cartViewDTO)) {
            cartViewDTO.getCartItems().forEach(cartItemDTO -> cartItemDTO.getProductCartItems().forEach(productCartItemDTO -> {
                String productImageUrl = fileService.getPresignedUrl(productCartItemDTO.getProductView().getProductImages().get(0).getImageUrl());
                productCartItemDTO.getProductView().getProductImages().get(0).setImageUrl(productImageUrl);
            }));
        }
        return cartViewDTO;
    }

    @Override
    public void clearCartViewByUserId(String userId) {
        CartView cartView = cartViewRepository.findByUserId(userId)
            .orElseThrow(() -> new NotFoundException(MessageError.CART_NOT_FOUND));
        cartView.clearCart();
        cartViewRepository.save(cartView);
    }
}
