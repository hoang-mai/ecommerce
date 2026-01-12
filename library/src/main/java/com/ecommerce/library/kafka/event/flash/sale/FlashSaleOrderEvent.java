package com.ecommerce.library.kafka.event.flash.sale;

import lombok.*;

import java.util.List;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FlashSaleOrderEvent {
    private Long userId;
    private String receiverName;
    private String address;
    private String phoneNumber;
    private List<FlashSaleOrderItemEvent> items;
}

