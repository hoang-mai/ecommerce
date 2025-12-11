package com.ecommerce.read.service;

import com.ecommerce.library.enumeration.ShopStatus;
import com.ecommerce.library.kafka.event.review.CreateReviewViewEvent;
import com.ecommerce.library.kafka.event.shop.CreateShopEvent;
import com.ecommerce.library.kafka.event.shop.UpdateShopStatusEvent;
import com.ecommerce.library.utils.PageResponse;
import com.ecommerce.read.dto.OwnerViewStatisticDTO;
import com.ecommerce.read.entity.ShopView;

public interface ShopViewService {
    void createShopView(CreateShopEvent createShopEvent);

    void updateShopStatusView(UpdateShopStatusEvent updateShopStatusEvent);
    PageResponse<ShopView> getShops(ShopStatus status,
                                      String keyword, int pageNo, int pageSize,
                                      String sortBy, String sortDir);

    PageResponse<ShopView> getShopsByCurrentOwner(ShopStatus status, String keyword, int pageNo, int pageSize, String sortBy, String sortDir);

    ShopView getShopById(Long shopId,boolean isOwner);

    OwnerViewStatisticDTO getOverviewStatistics();
}
