package com.ecommerce.read.service;

import com.ecommerce.library.enumeration.ShopStatus;
import com.ecommerce.library.kafka.event.review.CreateReviewViewEvent;
import com.ecommerce.library.kafka.event.shop.CreateShopEvent;
import com.ecommerce.library.kafka.event.shop.UpdateShopStatusEvent;
import com.ecommerce.library.utils.PageResponse;
import com.ecommerce.read.dto.NewShopViewStatisticDTO;
import com.ecommerce.read.dto.OwnerViewStatisticDTO;
import com.ecommerce.read.dto.ShopViewStatisticDTO;
import com.ecommerce.read.entity.ShopView;

import java.time.Instant;
import java.util.List;

public interface ShopViewService {
    void createShopView(CreateShopEvent createShopEvent);

    void updateShopStatusView(UpdateShopStatusEvent updateShopStatusEvent);
    PageResponse<ShopView> getShops(ShopStatus status,
                                      String keyword, int pageNo, int pageSize,
                                      String sortBy, String sortDir);

    PageResponse<ShopView> getShopsByCurrentOwner(ShopStatus status, String keyword, int pageNo, int pageSize, String sortBy, String sortDir);

    ShopView getShopById(Long shopId,boolean isOwner);

    OwnerViewStatisticDTO getOverviewStatistics();

    List<ShopViewStatisticDTO> getTopShopsByRevenue(Boolean isOwner,Instant nowDate, String type);

    List<NewShopViewStatisticDTO> getStatisticsByDateRange(Instant startDate, Instant endDate);
}
