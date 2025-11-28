package com.ecommerce.read.service.impl;

import com.ecommerce.library.component.UserHelper;
import com.ecommerce.library.exception.NotFoundException;
import com.ecommerce.library.kafka.event.cart.CreateCartEvent;
import com.ecommerce.library.kafka.event.cart.UpdateProductCartItemEvent;
import com.ecommerce.library.kafka.event.cart.DeleteCartItemEvent;
import com.ecommerce.library.utils.FnCommon;
import com.ecommerce.library.utils.MessageError;
import com.ecommerce.read.dto.CartViewDTO;
import com.ecommerce.read.entity.CartView;
import com.ecommerce.read.repository.CartViewRepository;
import com.ecommerce.read.repository.impl.CartViewRepositoryImpl;
import com.ecommerce.read.service.CartViewService;
import com.ecommerce.read.service.FileService;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartViewServiceImpl implements CartViewService {

    private final UserHelper userHelper;
    private final CartViewRepository cartViewRepository;
    private final CartViewRepositoryImpl cartViewRepositoryImpl;
    private final FileService fileService;

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
                                        .productId(String.valueOf(itemEvent.getProductId()))
                                        .productCartItems(itemEvent.getCreateProductCartItemEvents().stream().map(
                                                productCartItemEvent -> CartView.ProductCartItem.builder()
                                                        ._id(String.valueOf(productCartItemEvent.getProductCartItemId()))
                                                        .productVariantId(String.valueOf(productCartItemEvent.getProductVariantId()))
                                                        .quantity(productCartItemEvent.getQuantity())
                                                        .build()
                                        ).toList())
                                        .build()
                        ).toList()
                )
                .build();
        cartViewRepository.save(cartView);
    }

    @Override
    public void updateCartItem(UpdateProductCartItemEvent event) {
        cartViewRepositoryImpl.updateCartItem(event);
    }

    @Override
    public void deleteCartItem(DeleteCartItemEvent event) {

        if(FnCommon.isNotNull(event.getIsDeletedAllItems()) && event.getIsDeletedAllItems()){
            CartView cartView = cartViewRepository.findById(String.valueOf(event.getCartId()))
                    .orElseThrow(() -> new NotFoundException(MessageError.CART_NOT_FOUND));
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
                cartView.removeCartItem(cartItemToRemove);
            }
            cartViewRepository.save(cartView);
        }
    }

    @Override
    public CartViewDTO getCurrentUserCart() {
        CartViewDTO cartViewDTO= cartViewRepositoryImpl.findCartViewDTOByUserId(String.valueOf(userHelper.getCurrentUserId()));
        if(cartViewDTO != null){
            cartViewDTO.getCartItems().forEach(cartItemDTO -> {
                if(cartItemDTO.getProductView() != null && cartItemDTO.getProductView().getProductImages() != null){
                    cartItemDTO.getProductView().getProductImages().forEach(productImage -> productImage.setImageUrl(fileService.getPresignedUrl(productImage.getImageUrl())));
                }
            });
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
