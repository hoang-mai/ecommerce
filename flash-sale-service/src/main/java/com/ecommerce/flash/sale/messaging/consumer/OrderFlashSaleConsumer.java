package com.ecommerce.flash.sale.messaging.consumer;

import com.ecommerce.flash.sale.service.OrderFlashSaleService;
import com.ecommerce.library.kafka.event.order.CreateListOrderEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.ecommerce.flash.sale.service.FlashSaleProductService;
import com.ecommerce.library.kafka.event.product.UpdateFlashSaleProductStockEvent;
import com.ecommerce.library.kafka.event.flashsale.RestoreFlashSaleStockEvent;
import static com.ecommerce.library.kafka.Constant.CREATE_ORDER_FLASH_SALE_TOPIC;
import static com.ecommerce.library.kafka.Constant.FLASH_SALE_SERVICE_GROUP;
import static com.ecommerce.library.kafka.Constant.RESTORE_FLASH_SALE_STOCK_TOPIC;

@Service
@RequiredArgsConstructor
public class OrderFlashSaleConsumer {
    private final OrderFlashSaleService orderFlashSaleService;
    private final FlashSaleProductService flashSaleProductService;

    @KafkaListener(topics = CREATE_ORDER_FLASH_SALE_TOPIC, groupId = FLASH_SALE_SERVICE_GROUP)
    public void listenCreateFlashSaleOrderEvent(CreateListOrderEvent createListOrderEvent) {
        orderFlashSaleService.processFlashSaleOrder(createListOrderEvent);
    }

    public void listenUpdateFlashSaleProductStockEvent(UpdateFlashSaleProductStockEvent event) {
        flashSaleProductService.handleUpdateFlashSaleProductStock(event.getFlashSaleProductId(), event.getStock());
    }

    @KafkaListener(topics = RESTORE_FLASH_SALE_STOCK_TOPIC, groupId = FLASH_SALE_SERVICE_GROUP)
    public void listenRestoreFlashSaleStockEvent(RestoreFlashSaleStockEvent event) {
        orderFlashSaleService.restoreFlashSaleStock(event);
    }
}
