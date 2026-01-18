package com.ecommerce.flash.sale.service;

import com.ecommerce.library.kafka.event.flashsale.RestoreFlashSaleStockEvent;
import com.ecommerce.library.kafka.event.order.CreateListOrderEvent;

public interface OrderFlashSaleService {

    void processFlashSaleOrder(CreateListOrderEvent createOrderEvent);

    void restoreFlashSaleStock(RestoreFlashSaleStockEvent event);
}
