package com.ecommerce.read.service;

import com.ecommerce.library.enumeration.OrderStatus;
import com.ecommerce.library.kafka.event.order.CreateOrderEvent;
import com.ecommerce.library.kafka.event.order.OrderStatusEvent;
import com.ecommerce.library.utils.PageResponse;
import com.ecommerce.read.entity.OrderView;

public interface OrderViewService {
    void createOrderView(CreateOrderEvent createOrderViewEvent);

    void updateOrderStatusView(OrderStatusEvent orderStatusEvent);

    PageResponse<OrderView> getOrderViews(OrderStatus orderStatus, String keyword, int pageNo, int pageSize, String sortBy, String sortDir);
}
