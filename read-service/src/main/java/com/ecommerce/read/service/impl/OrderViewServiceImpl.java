package com.ecommerce.read.service.impl;

import com.ecommerce.library.component.UserHelper;
import com.ecommerce.library.enumeration.OrderStatus;
import com.ecommerce.library.exception.NotFoundException;
import com.ecommerce.library.kafka.event.order.CreateOrderEvent;
import com.ecommerce.library.kafka.event.order.OrderStatusEvent;
import com.ecommerce.library.utils.MessageError;
import com.ecommerce.library.utils.PageResponse;
import com.ecommerce.read.entity.OrderView;
import com.ecommerce.read.repository.OrderViewRepository;
import com.ecommerce.read.repository.impl.OrderViewRepositoryImpl;
import com.ecommerce.read.service.CartViewService;
import com.ecommerce.read.service.OrderViewService;
import com.ecommerce.read.service.ProductViewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderViewServiceImpl implements OrderViewService {

    private final OrderViewRepository orderViewRepository;
    private final OrderViewRepositoryImpl orderViewRepositoryImpl;
    private final CartViewService cartViewService;
    private final ProductViewService productViewService;
    private final UserHelper userHelper;

    @Override
    public void createOrderView(CreateOrderEvent createOrderViewEvent) {
        orderViewRepository.save(OrderView.builder()
                ._id(String.valueOf(createOrderViewEvent.getOrderId()))
                .userId(String.valueOf(createOrderViewEvent.getUserId()))
                .orderStatus(createOrderViewEvent.getOrderStatus())
                .totalPrice(createOrderViewEvent.getTotalPrice())
                .receiverName(createOrderViewEvent.getReceiverName())
                .address(createOrderViewEvent.getAddress())
                .phoneNumber(createOrderViewEvent.getPhoneNumber())
                .createdAt(createOrderViewEvent.getCreatedAt())
                .updatedAt(createOrderViewEvent.getUpdatedAt())
                .orderItems(createOrderViewEvent.getCreateOrderItemEventList().stream().map(
                        orderItemEvent -> OrderView.OrderItem.builder()
                                ._id(String.valueOf(orderItemEvent.getOrderItemId()))
                                .productId(String.valueOf(orderItemEvent.getProductId()))
                                .productName(orderItemEvent.getProductName())
                                .productImageList(orderItemEvent.getCreateProductImageList().stream().map(
                                        imageEvent -> OrderView.ProductImage.builder()
                                                .imageUrl(imageEvent.getImageUrl())
                                                .build()).toList())
                                .productVariants(orderItemEvent.getCreateProductOrderItemEvents().stream().map(
                                        productVariantEvent -> OrderView.ProductVariant.builder()
                                                ._id(String.valueOf(productVariantEvent.getProductVariantId()))
                                                .quantity(productVariantEvent.getQuantity())
                                                .price(productVariantEvent.getPrice())
                                                .productAttributes(productVariantEvent.getCreateProductAttributeList().stream().map(
                                                        attributeEvent -> OrderView.ProductAttribute.builder()
                                                                .attributeName(attributeEvent.getAttributeName())
                                                                .attributeValue(attributeEvent.getAttributeValue())
                                                                .build()).toList())
                                                .build()).toList())
                                .build()).toList())
                .build());
        if (createOrderViewEvent.getOrderStatus() == OrderStatus.PAID) {
            cartViewService.clearCartViewByUserId(String.valueOf(createOrderViewEvent.getUserId()));
            productViewService.updateStockAfterCreateOrder(createOrderViewEvent);
        }
    }

    @Override
    public void updateOrderStatusView(OrderStatusEvent orderStatusEvent) {
        OrderView orderView = orderViewRepository.findById(String.valueOf(orderStatusEvent.getOrderId()))
                .orElseThrow(() -> new NotFoundException(MessageError.ORDER_NOT_FOUND));
        orderView.setOrderStatus(orderStatusEvent.getOrderStatus());
        if (orderStatusEvent.getOrderStatus() == OrderStatus.CANCELLED || orderStatusEvent.getOrderStatus() == OrderStatus.RETURNED) {
            orderView.setReason(orderStatusEvent.getReason());
        }
        orderViewRepository.save(orderView);
    }

    @Override
    public PageResponse<OrderView> getOrderViews(OrderStatus orderStatus, String keyword, int pageNo, int pageSize, String sortBy, String sortDir) {
        Long currentUserId = userHelper.getCurrentUserId();
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
        Page<OrderView> orderViewPage = orderViewRepositoryImpl.getOrderView(currentUserId,orderStatus, keyword, pageable);

        return PageResponse.<OrderView>builder()
                .data(orderViewPage.getContent())
                .pageNo(orderViewPage.getNumber())
                .pageSize(orderViewPage.getSize())
                .totalElements(orderViewPage.getTotalElements())
                .totalPages(orderViewPage.getTotalPages())
                .hasNextPage(orderViewPage.hasNext())
                .hasPreviousPage(orderViewPage.hasPrevious())
                .build();
    }
}
