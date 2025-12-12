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
import com.ecommerce.read.dto.CreateInteractionRequest;
import com.ecommerce.read.dto.InteractionDTO;
import com.ecommerce.read.entity.CartView;
import com.ecommerce.read.entity.Interaction;
import com.ecommerce.read.repository.CartViewRepository;
import com.ecommerce.read.repository.impl.CartViewRepositoryImpl;
import com.ecommerce.read.service.CartViewService;
import com.ecommerce.read.service.FileService;
import com.ecommerce.read.service.InteractionService;
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
    private final InteractionService interactionService;

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
            CreateInteractionRequest interactionRequest = CreateInteractionRequest.builder()
                .userId(String.valueOf(event.getUserId()))
                .productId(String.valueOf(productCartItemEvent.getProductId()))
                .interactionType(Interaction.InteractionType.ADD_TO_CART)
                .metadata(InteractionDTO.InteractionMetadataDTO.builder()
                    .quantity(productCartItemEvent.getQuantity())
                    .build())
                .build();
            interactionService.createInteraction(interactionRequest);
        })));
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

            // Track remove interaction for all items before clearing
            CompletableFuture.runAsync(() -> cartView.getCartItems().forEach(cartItem ->
                cartItem.getProductCartItems().forEach(productCartItem -> {
                    CreateInteractionRequest interactionRequest = CreateInteractionRequest.builder()
                        .userId(cartView.getUserId())
                        .productId(productCartItem.getProductId())
                        .interactionType(Interaction.InteractionType.REMOVE_FROM_CART)
                        .metadata(InteractionDTO.InteractionMetadataDTO.builder()
                            .quantity(productCartItem.getQuantity())
                            .build())
                        .build();
                    interactionService.createInteraction(interactionRequest);
                })
            ));

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
                // Track remove interaction for all products in the cart item
                CartView.CartItem finalCartItemToRemove = cartItemToRemove;
                CompletableFuture.runAsync(() -> finalCartItemToRemove.getProductCartItems().forEach(productCartItem -> {
                    CreateInteractionRequest interactionRequest = CreateInteractionRequest.builder()
                        .userId(cartView.getUserId())
                        .productId(productCartItem.getProductId())
                        .interactionType(Interaction.InteractionType.REMOVE_FROM_CART)
                        .metadata(InteractionDTO.InteractionMetadataDTO.builder()
                            .quantity(productCartItem.getQuantity())
                            .build())
                        .build();
                    interactionService.createInteraction(interactionRequest);
                }));

                cartView.removeCartItem(cartItemToRemove);
            }
            cartViewRepository.save(cartView);
        }
    }

    @Override
    public void deleteProductCartItem(DeleteProductCartItemEvent event) {
        CartView cartView = cartViewRepository.findById(String.valueOf(event.getCartId()))
                .orElseThrow(() -> new NotFoundException(MessageError.CART_NOT_FOUND));

        // Tìm cartItem chứa productCartItem cần xóa
        CartView.CartItem cartItem = cartView.getCartItems().stream()
                .filter(item -> item.get_id().equals(String.valueOf(event.getCartItemId())))
                .findFirst()
                .orElse(null);

        if (FnCommon.isNotNull(cartItem)) {
            if (event.getIsDeleteCartItem()) {
                // Track remove interaction for all products in cart item
                CartView.CartItem finalCartItem = cartItem;
                CompletableFuture.runAsync(() -> finalCartItem.getProductCartItems().forEach(productCartItem -> {
                    CreateInteractionRequest interactionRequest = CreateInteractionRequest.builder()
                        .userId(cartView.getUserId())
                        .productId(productCartItem.getProductId())
                        .interactionType(Interaction.InteractionType.REMOVE_FROM_CART)
                        .metadata(InteractionDTO.InteractionMetadataDTO.builder()
                            .quantity(productCartItem.getQuantity())
                            .build())
                        .build();
                    interactionService.createInteraction(interactionRequest);
                }));

                // Nếu cần xóa luôn cartItem (không còn sản phẩm nào)
                cartView.removeCartItem(cartItem);
            } else {
                // Chỉ xóa productCartItem
                CartView.ProductCartItem productCartItemToRemove = cartItem.getProductCartItems().stream()
                        .filter(pci -> pci.get_id().equals(String.valueOf(event.getProductCartItemId())))
                        .findFirst()
                        .orElse(null);

                if (FnCommon.isNotNull(productCartItemToRemove)) {
                    // Track remove interaction for this specific product
                    CartView.ProductCartItem finalProductCartItem = productCartItemToRemove;
                    CompletableFuture.runAsync(() -> {
                        CreateInteractionRequest interactionRequest = CreateInteractionRequest.builder()
                            .userId(cartView.getUserId())
                            .productId(finalProductCartItem.getProductId())
                            .interactionType(Interaction.InteractionType.REMOVE_FROM_CART)
                            .metadata(InteractionDTO.InteractionMetadataDTO.builder()
                                .quantity(finalProductCartItem.getQuantity())
                                .build())
                            .build();
                        interactionService.createInteraction(interactionRequest);
                    });

                    cartItem.getProductCartItems().remove(productCartItemToRemove);
                }
            }
            cartViewRepository.save(cartView);
        }
    }

    @Override
    public CartViewDTO getCurrentUserCart() {
        CartViewDTO cartViewDTO= cartViewRepositoryImpl.findCartViewDTOByUserId(String.valueOf(userHelper.getCurrentUserId()));
        if(FnCommon.isNotNull(cartViewDTO)){
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
