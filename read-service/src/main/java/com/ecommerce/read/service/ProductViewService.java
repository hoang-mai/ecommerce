package com.ecommerce.read.service;

import com.ecommerce.library.enumeration.ProductStatus;
import com.ecommerce.library.enumeration.RatingNumber;
import com.ecommerce.library.kafka.event.order.CreateListOrderEvent;
import com.ecommerce.library.kafka.event.order.CreateOrderEvent;
import com.ecommerce.library.kafka.event.product.CreateProductEvent;
import com.ecommerce.library.kafka.event.product.UpdateProductStatusEvent;
import com.ecommerce.library.kafka.event.product.UpdateProductVariantStatusEvent;
import com.ecommerce.library.kafka.event.review.CreateReviewViewEvent;
import com.ecommerce.library.kafka.event.shop.UpdateShopStatusEvent;
import com.ecommerce.library.utils.PageResponse;
import com.ecommerce.read.dto.ProductViewStatisticDTO;
import com.ecommerce.read.entity.ProductView;

import java.time.LocalDateTime;
import java.util.List;


public interface ProductViewService {
    void createProductEvent(CreateProductEvent createProductEvent);

    void updateProductStatus(UpdateProductStatusEvent event);

    void updateProductVariantStatus(UpdateProductVariantStatusEvent event);

    PageResponse<ProductView> searchProducts(Boolean isOwner, Long shopId, Long categoryId, ProductStatus status, String keyword, Integer star, Double startPrice, Double endPrice, int pageNo, int pageSize, String sortBy, String sortDir);

    ProductView getProductById(Long productId,boolean isOwner);

    void updateStockAfterCreateOrder(CreateListOrderEvent createListOrderEvent);

    void updateShopStatusInProductViews(UpdateShopStatusEvent updateShopStatusEvent);

    void updateRating(Long productId, RatingNumber rating, Boolean isUpdate, RatingNumber oldRating, Boolean isDelete);

    List<ProductViewStatisticDTO> getProductStatistics(String shopId, Boolean isOwner, LocalDateTime nowDate);
}
