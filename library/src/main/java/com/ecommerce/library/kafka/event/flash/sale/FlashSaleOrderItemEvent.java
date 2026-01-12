package com.ecommerce.library.kafka.event.flash.sale;

import lombok.*;

import java.util.List;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FlashSaleOrderItemEvent {
    private Long shopId;
    private Long cartItemId;
    private String note;
    private List<FlashSaleProductOrderItemEvent> productOrderItems;
}

