package com.ecommerce.product.service.impl;

import com.ecommerce.library.kafka.event.flash.sale.FlashSaleProductEvent;
import com.ecommerce.product.entity.FlashSaleProductCache;
import com.ecommerce.product.repository.FlashSaleProductCacheRepository;
import com.ecommerce.product.service.FlashSaleProductCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FlashSaleProductCacheServiceImpl implements FlashSaleProductCacheService {

    private final FlashSaleProductCacheRepository flashSaleProductCacheRepository;
    @Override
    public void createFlashSaleProductCache(FlashSaleProductEvent flashSaleProductEvent) {
        FlashSaleProductCache flashSaleProductCache = FlashSaleProductCache.builder()
            .flashSaleProductId(flashSaleProductEvent.getFlashSaleProductId())
            .productId(flashSaleProductEvent.getProductId())
            .productVariantId(flashSaleProductEvent.getProductVariantId())
            .startTime(flashSaleProductEvent.getStartTime())
            .endTime(flashSaleProductEvent.getEndTime())
            .build();
        flashSaleProductCacheRepository.save(flashSaleProductCache);
    }

    @Override
    public void deleteFlashSaleProductCache(Long flashSaleProductId) {
        flashSaleProductCacheRepository.deleteById(flashSaleProductId);
    }
}
