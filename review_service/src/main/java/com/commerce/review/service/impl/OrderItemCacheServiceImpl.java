package com.commerce.review.service.impl;

import com.ecommerce.library.kafka.event.order.CreateOrderEvent;
import com.ecommerce.library.kafka.event.order.CreateOrderItemEvent;
import com.commerce.review.entity.OrderItemCache;
import com.commerce.review.repository.OrderItemCacheRepository;
import com.commerce.review.service.OrderItemCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderItemCacheServiceImpl implements OrderItemCacheService {

    private final OrderItemCacheRepository orderItemCacheRepository;

    @Override
    public void createOrderItemCache(CreateOrderEvent createOrderEvent) {
        if (createOrderEvent == null || createOrderEvent.getCreateOrderItemEventList() == null) return;
        for (CreateOrderItemEvent item : createOrderEvent.getCreateOrderItemEventList()) {
            orderItemCacheRepository.save(OrderItemCache.builder()
                    .orderItemId(item.getOrderItemId())
                    .productId(item.getProductId())
                    .productVariantId(item.getProductVariantId())
                    .userId(createOrderEvent.getUserId())
                    .build());
        }
    }
}

