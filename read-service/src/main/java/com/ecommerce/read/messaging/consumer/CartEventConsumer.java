package com.ecommerce.read.messaging.consumer;

import com.ecommerce.library.kafka.event.cart.CreateCartEvent;
import com.ecommerce.library.kafka.event.cart.DeleteCartItemEvent;
import com.ecommerce.library.kafka.event.cart.DeleteProductCartItemEvent;
import com.ecommerce.library.kafka.event.cart.UpdateProductCartItemEvent;
import com.ecommerce.read.service.CartViewService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import static com.ecommerce.library.kafka.Constant.*;

@Service
@RequiredArgsConstructor
public class CartEventConsumer {
    private final CartViewService cartViewService;

    @KafkaListener(topics = CREATE_CART_TOPIC, groupId = READ_SERVICE_GROUP)
    public void listen(CreateCartEvent event) {
        cartViewService.createCart(event);
    }

    @KafkaListener(topics = UPDATE_CART_ITEM_TOPIC, groupId = READ_SERVICE_GROUP)
    public void listen(UpdateProductCartItemEvent event) {
        cartViewService.updateCartItem(event);
    }

    @KafkaListener(topics = DELETE_CART_ITEM_TOPIC, groupId = READ_SERVICE_GROUP)
    public void listen(DeleteCartItemEvent event) {
        cartViewService.deleteCartItem(event);
    }

    @KafkaListener(topics = DELETE_PRODUCT_CART_ITEM_TOPIC, groupId = READ_SERVICE_GROUP)
    public void listen(DeleteProductCartItemEvent event) {
        cartViewService.deleteProductCartItem(event);
    }
}
