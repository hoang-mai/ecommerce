package com.ecommerce.read.service.impl;

import com.ecommerce.library.enumeration.OrderStatus;
import com.ecommerce.library.exception.NotFoundException;
import com.ecommerce.library.kafka.event.order.CreateOrderEvent;
import com.ecommerce.library.kafka.event.order.OrderStatusEvent;
import com.ecommerce.library.utils.MessageError;
import com.ecommerce.library.utils.PageResponse;
import com.ecommerce.read.entity.OrderView;
import com.ecommerce.read.repository.OrderViewRepository;
import com.ecommerce.read.service.OrderViewService;
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

    @Override
    public void createOrderView(CreateOrderEvent createOrderViewEvent) {
        orderViewRepository.save(OrderView.builder()
                .orderId(String.valueOf(createOrderViewEvent.getOrderId()))
                .userId(createOrderViewEvent.getUserId())
                .orderStatus(createOrderViewEvent.getOrderStatus())
                .totalPrice(createOrderViewEvent.getTotalPrice())
                .address(createOrderViewEvent.getAddress())
                .phoneNumber(createOrderViewEvent.getPhoneNumber())
                .orderItems(createOrderViewEvent.getCreateOrderItemEventList().stream().map(
                                orderItemEvent -> OrderView.OrderItem.builder()
                                        .orderItemId(orderItemEvent.getOrderItemId())
                                        .productId(orderItemEvent.getProductId())
                                        .productName(orderItemEvent.getProductName())
                                        .productImageList(orderItemEvent.getCreateProductImageList().stream().map(
                                                imageEvent -> OrderView.ProductImage.builder()
                                                        .imageUrl(imageEvent.getImageUrl())
                                                        .build()).toList())
                                        .productVariants(orderItemEvent.getCreateProductOrderItemEvents().stream().map(
                                                productVariantEvent -> OrderView.ProductVariant.builder()
                                                        .productVariantId(productVariantEvent.getProductVariantId())
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
    }

    @Override
    public void updateOrderStatusView(OrderStatusEvent orderStatusEvent) {
        OrderView orderView = orderViewRepository.findById(orderStatusEvent.getOrderId())
                .orElseThrow(() -> new NotFoundException(MessageError.ORDER_NOT_FOUND));
        orderView.setOrderStatus(orderStatusEvent.getOrderStatus());
        if(orderStatusEvent.getOrderStatus() == OrderStatus.CANCELLED || orderStatusEvent.getOrderStatus() == OrderStatus.RETURNED){
            orderView.setReason(orderStatusEvent.getReason());
        }
        orderViewRepository.save(orderView);
    }

    @Override
    public PageResponse<OrderView> getOrderViews(OrderStatus orderStatus, String keyword, int pageNo, int pageSize, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
        Page<OrderView> orderViewPage = orderViewRepository.getOrderView(orderStatus, keyword, pageable);

        return PageResponse.<OrderView>builder()
                .data(orderViewPage.getContent())
                .pageNo(orderViewPage.getNumber())
                .pageSize(orderViewPage.getSize())
                .totalElements(orderViewPage.getTotalElements())
                .totalPages(orderViewPage.getTotalPages())
                .build();
    }
}
