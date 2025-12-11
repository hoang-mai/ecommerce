package com.ecommerce.read.service;

import com.ecommerce.library.enumeration.OrderStatus;
import com.ecommerce.library.kafka.event.order.CreateListOrderEvent;
import com.ecommerce.library.kafka.event.order.OrderStatusEvent;
import com.ecommerce.library.utils.PageResponse;
import com.ecommerce.read.dto.OrderViewStatisticDTO;
import com.ecommerce.read.entity.OrderView;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface OrderViewService {
    void createOrderView(CreateListOrderEvent createListOrderEvent);

    void updateOrderStatusView(OrderStatusEvent orderStatusEvent);

    PageResponse<OrderView> getOrderViews(String shopId,Boolean isOwner, OrderStatus orderStatus, String keyword, String productId, int pageNo, int pageSize, String sortBy, String sortDir);

    Map<OrderStatus, Long> getOrderStatistics(String shopId, Boolean isOwner, Integer month, Integer year);

    List<OrderViewStatisticDTO> getOrderStatisticsByDateRange(String shopId, Boolean isOwner, LocalDateTime fromDate, LocalDateTime toDate);
}

