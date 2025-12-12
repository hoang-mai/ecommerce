package com.ecommerce.chat.service.impl;

import com.ecommerce.chat.entity.ShopCache;
import com.ecommerce.chat.repository.ShopCacheRepository;
import com.ecommerce.chat.service.ShopCacheService;
import com.ecommerce.library.exception.NotFoundException;
import com.ecommerce.library.kafka.event.shop.CreateShopEvent;
import com.ecommerce.library.kafka.event.shop.UpdateShopStatusEvent;
import com.ecommerce.library.utils.MessageError;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class ShopCacheServiceImpl implements ShopCacheService {

    private final ShopCacheRepository shopCacheRepository;

    @Override
    public void createShopCache(CreateShopEvent createShopEvent) {
        shopCacheRepository.save(
            ShopCache.builder()
                ._id(String.valueOf(createShopEvent.getShopId()))
                .shopName(createShopEvent.getShopName())
                .logoUrl(createShopEvent.getLogoUrl())
                .ownerId(String.valueOf(createShopEvent.getOwnerId()))
                .shopStatus(createShopEvent.getShopStatus())
                .createdAt(createShopEvent.getCreatedAt())
                .build()
        );
    }

    @Override
    public void updateShopCacheStatus(UpdateShopStatusEvent updateShopStatusEvent) {
        ShopCache shopCache = shopCacheRepository.findById(String.valueOf(updateShopStatusEvent.getShopId()))
            .orElseThrow(() -> new NotFoundException(MessageError.SHOP_NOT_FOUND));
        shopCache.setShopStatus(updateShopStatusEvent.getShopStatus());

        shopCacheRepository.save(shopCache);
    }

}

