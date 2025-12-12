package com.ecommerce.review.service.impl;

import com.ecommerce.library.kafka.event.order.CreateListOrderEvent;
import com.ecommerce.library.kafka.event.order.CreateOrderEvent;
import com.ecommerce.library.kafka.event.order.CreateOrderItemEvent;
import com.ecommerce.review.entity.OrderItemCache;
import com.ecommerce.review.repository.OrderItemCacheRepository;
import com.ecommerce.review.service.OrderItemCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderItemCacheServiceImpl implements OrderItemCacheService {

    private final OrderItemCacheRepository orderItemCacheRepository;

    @Override
    public void createOrderItemCache(CreateListOrderEvent createListOrderEvent) {
        for (CreateOrderEvent createOrderEvent : createListOrderEvent.getCreateOrderEventList()) {
            for (CreateOrderItemEvent createOrderItemEvent : createOrderEvent.getCreateOrderItemEventList()) {
                OrderItemCache orderItemCache = OrderItemCache.builder()
                    .orderItemId(createOrderItemEvent.getOrderItemId())
                    .productId(createOrderItemEvent.getProductId())
                    .productVariantId(createOrderItemEvent.getProductVariantId())
                    .userId(createListOrderEvent.getUserId())
                    .build();
                orderItemCacheRepository.save(orderItemCache);
            }
        }
    }
}

