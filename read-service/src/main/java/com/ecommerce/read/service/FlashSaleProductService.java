package com.ecommerce.read.service;

import com.ecommerce.library.kafka.event.flash.sale.FlashSaleProductEvent;
import com.ecommerce.library.utils.PageResponse;
import com.ecommerce.read.dto.FlashSaleStatisticDTO;
import com.ecommerce.read.entity.FlashSaleProductView;

public interface FlashSaleProductService {
    void createFlashSaleProduct(FlashSaleProductEvent event);

    void updateFlashSaleProduct(FlashSaleProductEvent event);

    void deleteFlashSaleProduct(Long flashSaleProductId);

    PageResponse<FlashSaleProductView> getFlashSaleProducts(
            String flashSaleCampaignId,
            String shopId,
            Boolean isOwner,
            int pageNo,
            int pageSize,
            String sortBy,
            String sortDir);

    PageResponse<FlashSaleProductView> getCurrentFlashSaleProducts(
            int pageNo,
            int pageSize,
            String sortBy,
            String sortDir);

    FlashSaleStatisticDTO getFlashSaleProductStatistics(String flashSaleCampaignId, Boolean isOwner);

    void updateFlashSaleProductSold(String flashSaleProductId, Integer quantity, java.math.BigDecimal totalFinalPrice);

    void restoreFlashSaleProductStock(com.ecommerce.library.kafka.event.flashsale.RestoreFlashSaleStockEvent event);
}
