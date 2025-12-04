package com.ecommerce.order.messaging.producer;

import com.ecommerce.library.kafka.event.cart.CreateCartEvent;
import com.ecommerce.library.kafka.event.cart.DeleteCartItemEvent;
import com.ecommerce.library.kafka.event.cart.DeleteProductCartItemEvent;
import com.ecommerce.library.kafka.event.cart.UpdateProductCartItemEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import static com.ecommerce.library.kafka.Constant.*;

@Service
@RequiredArgsConstructor
public class CartEventProducer {
    private final KafkaTemplate<Long, CreateCartEvent> createCartEventKafkaTemplate;
    private final KafkaTemplate<Long, UpdateProductCartItemEvent> updateProductCartItemEventKafkaTemplate;
    private final KafkaTemplate<Long, DeleteCartItemEvent> deleteCartItemEventKafkaTemplate;
    private final KafkaTemplate<Long, DeleteProductCartItemEvent> deleteProductCartItemEventKafkaTemplate;

    public void send(CreateCartEvent createCartEvent) {
        createCartEventKafkaTemplate.send(CREATE_CART_TOPIC, createCartEvent.getCartId(), createCartEvent);
    }

    public void send(UpdateProductCartItemEvent updateProductCartItemEvent) {
        updateProductCartItemEventKafkaTemplate.send(UPDATE_CART_ITEM_TOPIC, updateProductCartItemEvent.getCartId(), updateProductCartItemEvent);
    }

    public void send(DeleteCartItemEvent deleteCartItemEvent) {
        deleteCartItemEventKafkaTemplate.send(DELETE_CART_ITEM_TOPIC, deleteCartItemEvent.getCartId(), deleteCartItemEvent);
    }

    public void send(DeleteProductCartItemEvent deleteProductCartItemEvent) {
        deleteProductCartItemEventKafkaTemplate.send(DELETE_PRODUCT_CART_ITEM_TOPIC, deleteProductCartItemEvent.getCartId(), deleteProductCartItemEvent);
    }
}
