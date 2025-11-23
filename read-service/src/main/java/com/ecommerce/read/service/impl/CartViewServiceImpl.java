package com.ecommerce.read.service.impl;

import com.ecommerce.library.component.UserHelper;
import com.ecommerce.library.exception.NotFoundException;
import com.ecommerce.library.kafka.event.cart.CreateCartEvent;
import com.ecommerce.library.kafka.event.cart.UpdateProductCartItemEvent;
import com.ecommerce.library.kafka.event.cart.DeleteCartItemEvent;
import com.ecommerce.library.utils.FnCommon;
import com.ecommerce.library.utils.MessageError;
import com.ecommerce.read.dto.CartViewDTO;
import com.ecommerce.read.dto.ProductViewDTO;
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
                .cartId(String.valueOf(event.getCartId()))
                .userId(String.valueOf(event.getUserId()))
                .cartItems(
                        event.getCreateCartItemEventList().stream().map(
                                itemEvent -> CartView.CartItem.builder()
                                        .cartItemId(String.valueOf(itemEvent.getCartItemId()))
                                        .productId(String.valueOf(itemEvent.getProductId()))
                                        .productCartItems(itemEvent.getCreateProductCartItemEvents().stream().map(
                                                productCartItemEvent -> CartView.ProductCartItem.builder()
                                                        .productCartItemId(String.valueOf(productCartItemEvent.getProductCartItemId()))
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
                    .filter(item -> item.getCartItemId().equals(String.valueOf(event.getCartItemId())))
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
        CartViewDTO cartViewDTO= cartViewRepositoryImpl.findByUserId(String.valueOf(userHelper.getCurrentUserId()));
        if(cartViewDTO != null){
            cartViewDTO.getCartItems().forEach(cartItemDTO -> {
                if(cartItemDTO.getProductViewDTO() != null && cartItemDTO.getProductViewDTO().getProductImages() != null){
                    List<ProductViewDTO.ProductImageDTO> productImageDTOs = new ArrayList<>();
                    cartItemDTO.getProductViewDTO().getProductImages().forEach(imageDTO -> {
                        String imageUrl = fileService.getPresignedUrl(imageDTO.getUrl());
                        productImageDTOs.add(ProductViewDTO.ProductImageDTO.builder()
                                ._id(imageDTO.get_id())
                                .url(imageUrl)
                                .build());
                    });
                    cartItemDTO.getProductViewDTO().setProductImages(productImageDTOs);
                }
            });
        }
        return cartViewDTO;
    }
}
