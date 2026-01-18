package com.ecommerce.library.kafka.event.flashsale;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RestoreFlashSaleStockEvent {
    private Long userId;
    private List<RestoreFlashSaleItemEvent> restoreFlashSaleItems;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RestoreFlashSaleItemEvent {
        private Long flashSaleProductId;
        private Integer quantity;
        private java.math.BigDecimal totalFinalPrice;
    }
}
