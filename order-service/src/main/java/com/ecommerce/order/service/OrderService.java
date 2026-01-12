package com.ecommerce.order.service;

import com.ecommerce.library.kafka.event.flash.sale.FlashSaleOrderEvent;
import com.ecommerce.library.kafka.event.order.CreateListOrderStatusEvent;
import com.ecommerce.order.dto.ReqUpdateOrderStatus;
import com.ecommerce.order.dto.ResCreateOrderDTO;

public interface OrderService {

    /**
     * Create new order
     */
    void createOrder(ResCreateOrderDTO request);

    /**
     * Create new flash sale order from Kafka event
     */
    void createFlashSaleOrder(FlashSaleOrderEvent flashSaleOrderEvent);

    /**
     * Update order status
     */
    void updateOrderStatus(Long orderId, ReqUpdateOrderStatus reqUpdateOrderStatus);

    /**
     * Update order status
     */
    void updateOrderStatus(CreateListOrderStatusEvent createListOrderStatusEvent);

}

