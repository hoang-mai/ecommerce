package com.ecommerce.read.service;

import com.ecommerce.library.enumeration.OrderStatus;
import com.ecommerce.library.kafka.event.order.CreateOrderEvent;
import com.ecommerce.library.kafka.event.order.OrderStatusEvent;
import com.ecommerce.library.utils.PageResponse;
import com.ecommerce.read.entity.OrderView;

import java.util.List;

public interface OrderViewService {
    void createOrderView(List<CreateOrderEvent> createOrderEventList);

    void updateOrderStatusView(OrderStatusEvent orderStatusEvent);

    PageResponse<OrderView> getOrderViews(OrderStatus orderStatus, String keyword, int pageNo, int pageSize, String sortBy, String sortDir);
}
