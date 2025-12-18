package com.ecommerce.read.service.impl;

import com.ecommerce.library.component.UserHelper;
import com.ecommerce.library.enumeration.OrderStatus;
import com.ecommerce.library.exception.NotFoundException;
import com.ecommerce.library.kafka.event.order.CreateListOrderEvent;
import com.ecommerce.library.kafka.event.order.CreateListOrderStatusEvent;
import com.ecommerce.library.kafka.event.order.CreateOrderEvent;
import com.ecommerce.library.kafka.event.order.OrderStatusEvent;
import com.ecommerce.library.utils.FnCommon;
import com.ecommerce.library.utils.MessageError;
import com.ecommerce.library.utils.PageResponse;
import com.ecommerce.read.dto.CreateInteractionRequest;
import com.ecommerce.read.dto.InteractionDTO;
import com.ecommerce.read.dto.OrderViewStatisticDTO;
import com.ecommerce.read.dto.OrderViewStatisticRevenueDTO;
import com.ecommerce.read.entity.Interaction;
import com.ecommerce.read.entity.OrderView;
import com.ecommerce.read.repository.OrderViewRepository;
import com.ecommerce.read.repository.ShopViewRepository;
import com.ecommerce.read.repository.impl.OrderViewRepositoryImpl;
import com.ecommerce.read.service.CartViewService;
import com.ecommerce.read.service.FileService;
import com.ecommerce.read.service.InteractionService;
import com.ecommerce.read.service.OrderViewService;
import com.ecommerce.read.service.ProductViewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class OrderViewServiceImpl implements OrderViewService {

    private final ShopViewRepository shopViewRepository;
    private final OrderViewRepository orderViewRepository;
    private final OrderViewRepositoryImpl orderViewRepositoryImpl;
    private final CartViewService cartViewService;
    private final ProductViewService productViewService;
    private final UserHelper userHelper;
    private final FileService fileService;
    private final InteractionService interactionService;

    @Override
    public void createOrderView(CreateListOrderEvent createListOrderEvent) {
        for (CreateOrderEvent createOrderViewEvent : createListOrderEvent.getCreateOrderEventList()) {
            OrderView orderView = OrderView.builder()
                    ._id(String.valueOf(createOrderViewEvent.getOrderId()))
                    .userId(String.valueOf(createOrderViewEvent.getUserId()))
                    .ownerId(String.valueOf(createOrderViewEvent.getOwnerId()))
                    .shopId(String.valueOf(createOrderViewEvent.getShopId()))
                    .shopName(createOrderViewEvent.getShopName())
                    .shopLogoUrl(createOrderViewEvent.getShopLogoUrl())
                    .orderStatus(createOrderViewEvent.getOrderStatus())
                    .reason(createOrderViewEvent.getReason())
                    .receiverName(createOrderViewEvent.getReceiverName())
                    .address(createOrderViewEvent.getAddress())
                    .phoneNumber(createOrderViewEvent.getPhoneNumber())
                    .createdAt(createOrderViewEvent.getCreatedAt())
                    .updatedAt(createOrderViewEvent.getUpdatedAt())
                    .totalPrice(createOrderViewEvent.getTotalPrice())
                    .orderItems(createOrderViewEvent.getCreateOrderItemEventList().stream().map(createOrderItemEvent -> OrderView.OrderItem.builder()
                            ._id(String.valueOf(createOrderItemEvent.getOrderItemId()))
                            .productId(String.valueOf(createOrderItemEvent.getProductId()))
                            .productName(createOrderItemEvent.getProductName())
                            .productVariantId(String.valueOf(createOrderItemEvent.getProductVariantId()))
                            .productImageUrl(createOrderItemEvent.getProductImageUrl())
                            .totalPrice(createOrderItemEvent.getTotalPrice())
                            .totalDiscount(createOrderItemEvent.getTotalDiscount())
                            .totalFinalPrice(createOrderItemEvent.getTotalFinalPrice())
                            .quantity(createOrderItemEvent.getQuantity())
                            .price(createOrderItemEvent.getPrice())
                            .productAttributes(createOrderItemEvent.getCreateProductAttributeList().stream().map(attribute ->
                                    OrderView.ProductAttribute.builder()
                                            .attributeName(attribute.getAttributeName())
                                            .attributeValue(attribute.getAttributeValue())
                                            .build()).toList())
                            .build()).toList())
                    .build();
            orderViewRepository.save(orderView);

            // Track PURCHASE interaction for each product in the order
            String userId = String.valueOf(createOrderViewEvent.getUserId());
            CompletableFuture.runAsync(() -> createOrderViewEvent.getCreateOrderItemEventList().forEach(orderItem -> {
                CreateInteractionRequest interactionRequest = CreateInteractionRequest.builder()
                    .userId(userId)
                    .productId(String.valueOf(orderItem.getProductId()))
                    .interactionType(Interaction.InteractionType.PURCHASE)
                    .metadata(InteractionDTO.InteractionMetadataDTO.builder()
                        .quantity(orderItem.getQuantity())
                        .build())
                    .build();
                interactionService.createInteraction(interactionRequest);
            }));
        }
        cartViewService.clearCartViewByUserId(String.valueOf(createListOrderEvent.getUserId()));
        productViewService.updateStockAfterCreateOrder(createListOrderEvent);
    }

    @Override
    public void updateOrderStatusView(OrderStatusEvent orderStatusEvent) {
        OrderView orderView = orderViewRepository.findById(String.valueOf(orderStatusEvent.getOrderId()))
                .orElseThrow(() -> new NotFoundException(MessageError.ORDER_NOT_FOUND));

        OrderStatus previousStatus = orderView.getOrderStatus();
        orderView.setOrderStatus(orderStatusEvent.getOrderStatus());

        if (orderStatusEvent.getOrderStatus() == OrderStatus.CANCELLED || orderStatusEvent.getOrderStatus() == OrderStatus.RETURNED) {
            orderView.setReason(orderStatusEvent.getReason());
        }

        if (orderStatusEvent.getOrderStatus() == OrderStatus.COMPLETED && previousStatus != OrderStatus.COMPLETED) {
            updateSoldAndRevenue(orderView);
        }

        orderViewRepository.save(orderView);
    }

    private void updateSoldAndRevenue(OrderView orderView) {
        orderView.getOrderItems().forEach(orderItem -> {
            productViewService.updateProductSoldAndRevenue(
                orderItem.getProductId(),
                orderItem.getProductVariantId(),
                orderItem.getQuantity(),
                orderItem.getTotalFinalPrice()
            );
        });

        shopViewRepository.findById(orderView.getShopId()).ifPresent(shopView -> {
            if (shopView.getTotalSold() == null) {
                shopView.setTotalSold(0L);
            }
            if (shopView.getTotalRevenue() == null) {
                shopView.setTotalRevenue(BigDecimal.ZERO);
            }

            int totalQuantity = orderView.getOrderItems().stream()
                .mapToInt(OrderView.OrderItem::getQuantity)
                .sum();

            shopView.setTotalSold(shopView.getTotalSold() + totalQuantity);
            shopView.setTotalRevenue(shopView.getTotalRevenue().add(orderView.getTotalPrice()));
            shopViewRepository.save(shopView);
        });
    }

    @Override
    public PageResponse<OrderView> getOrderViews(String shopId,Boolean isOwner, OrderStatus orderStatus, String keyword, String productId, int pageNo, int pageSize, String sortBy, String sortDir) {

        Long currentUserId = userHelper.getCurrentUserId();
        if(FnCommon.isNotNullOrEmpty(shopId)){
            if(!shopViewRepository.existsBy_idAndOwnerId(shopId, String.valueOf(currentUserId))){
                throw new NotFoundException(MessageError.SHOP_NOT_FOUND);
            }
        }
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
        Page<OrderView> orderViewPage = orderViewRepositoryImpl.getOrderView(shopId,isOwner, currentUserId, orderStatus, keyword, productId, pageable);

        return PageResponse.<OrderView>builder()
                .data(orderViewPage.getContent().stream().peek(
                        orderView -> {
                            orderView.setShopLogoUrl(fileService.getPresignedUrl(orderView.getShopLogoUrl()));
                            orderView.getOrderItems().forEach(orderItem ->
                                    orderItem.setProductImageUrl(fileService.getPresignedUrl(orderItem.getProductImageUrl()))
                            );
                        }).toList())
                .pageNo(orderViewPage.getNumber())
                .pageSize(orderViewPage.getSize())
                .totalElements(orderViewPage.getTotalElements())
                .totalPages(orderViewPage.getTotalPages())
                .hasNextPage(orderViewPage.hasNext())
                .hasPreviousPage(orderViewPage.hasPrevious())
                .build();
    }

    @Override
    public Map<OrderStatus, Long> getOrderStatistics(String shopId, Boolean isOwner, Integer month, Integer year) {
        Long currentUserId = userHelper.getCurrentUserId();
        if (FnCommon.isNotNullOrEmpty(shopId)) {
            if (!shopViewRepository.existsBy_idAndOwnerId(shopId, String.valueOf(currentUserId))) {
                throw new NotFoundException(MessageError.SHOP_NOT_FOUND);
            }
        }
        return orderViewRepositoryImpl.getOrderCountByStatus(shopId, isOwner, currentUserId, month, year);
    }

    @Override
    public List<OrderViewStatisticDTO> getOrderStatisticsByDateRange(String shopId, Boolean isOwner, LocalDateTime fromDate, LocalDateTime toDate) {
        Long currentUserId = userHelper.getCurrentUserId();
        if (FnCommon.isNotNullOrEmpty(shopId)) {
            if (!shopViewRepository.existsBy_idAndOwnerId(shopId, String.valueOf(currentUserId))) {
                throw new NotFoundException(MessageError.SHOP_NOT_FOUND);
            }
        }
        return orderViewRepositoryImpl.getOrderStatisticsByDateRange(shopId, isOwner, currentUserId, fromDate, toDate);
    }

    @Override
    public void updateOrderStatusFromOrderEvent(CreateListOrderStatusEvent createListOrderStatusEvent) {
        createListOrderStatusEvent.getOrderStatusEventList().forEach(orderStatusEvent -> {
            OrderView orderView = orderViewRepository.findById(String.valueOf(orderStatusEvent.getOrderId()))
                    .orElseThrow(() -> new NotFoundException(MessageError.ORDER_NOT_FOUND));

            OrderStatus previousStatus = orderView.getOrderStatus();
            orderView.setOrderStatus(orderStatusEvent.getOrderStatus());

            if (orderStatusEvent.getOrderStatus() == OrderStatus.CANCELLED || orderStatusEvent.getOrderStatus() == OrderStatus.RETURNED) {
                orderView.setReason(orderStatusEvent.getReason());
                // Restore product stock when order fails
                if (previousStatus != OrderStatus.CANCELLED && previousStatus != OrderStatus.RETURNED) {
                    restoreProductStock(orderView);
                }
            }

            // Update sold and revenue when order is completed
            if (orderStatusEvent.getOrderStatus() == OrderStatus.COMPLETED && previousStatus != OrderStatus.COMPLETED) {
                updateSoldAndRevenue(orderView);
            }

            orderViewRepository.save(orderView);
        });
    }

    @Override
    public List<OrderViewStatisticRevenueDTO> getRevenueStatisticsByDateRange(String shopId, Boolean isOwner, LocalDateTime fromDate, LocalDateTime toDate) {
        Long currentUserId = userHelper.getCurrentUserId();
        if (FnCommon.isNotNullOrEmpty(shopId)) {
            if (!shopViewRepository.existsBy_idAndOwnerId(shopId, String.valueOf(currentUserId))) {
                throw new NotFoundException(MessageError.SHOP_NOT_FOUND);
            }
        }
        return orderViewRepositoryImpl.getOrderStatisticRevenuesByDateRange(shopId, isOwner, currentUserId, fromDate, toDate);
    }

    private void restoreProductStock(OrderView orderView) {
        orderView.getOrderItems().forEach(orderItem -> {
            productViewService.restoreProductStock(
                orderItem.getProductId(),
                orderItem.getProductVariantId(),
                orderItem.getQuantity()
            );
        });
    }
}

