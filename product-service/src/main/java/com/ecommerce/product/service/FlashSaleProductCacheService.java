package com.ecommerce.product.service;

import com.ecommerce.library.kafka.event.flash.sale.FlashSaleProductEvent;

public interface FlashSaleProductCacheService {
    void createFlashSaleProductCache(FlashSaleProductEvent flashSaleProductEvent);

    void deleteFlashSaleProductCache(Long flashSaleProductId);
}
