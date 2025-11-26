package com.ecommerce.read.service;

import com.ecommerce.library.enumeration.ProductStatus;
import com.ecommerce.library.kafka.event.order.CreateOrderEvent;
import com.ecommerce.library.kafka.event.product.CreateProductEvent;
import com.ecommerce.library.kafka.event.product.UpdateProductStatusEvent;
import com.ecommerce.library.kafka.event.product.UpdateProductVariantStatusEvent;
import com.ecommerce.library.kafka.event.shop.UpdateShopStatusEvent;
import com.ecommerce.library.utils.PageResponse;
import com.ecommerce.read.dto.ProductViewDTO;


public interface ProductViewService {
    void createProductEvent(CreateProductEvent createProductEvent);

    void updateProductStatus(UpdateProductStatusEvent event);

    void updateProductVariantStatus(UpdateProductVariantStatusEvent event);

    PageResponse<ProductViewDTO> searchProducts(boolean isOwner, Long shopId, Long categoryId, ProductStatus status, String keyword, int pageNo, int pageSize, String sortBy, String sortDir);

    ProductViewDTO getProductById(Long productId,boolean isOwner);

    void updateStockAfterCreateOrder(CreateOrderEvent createOrderViewEvent);

    void updateShopStatusInProductViews(UpdateShopStatusEvent updateShopStatusEvent);
}
