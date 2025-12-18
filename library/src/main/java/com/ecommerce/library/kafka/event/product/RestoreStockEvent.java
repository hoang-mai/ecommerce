package com.ecommerce.library.kafka.event.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RestoreStockEvent {
    private Long userId;
    private List<RestoreStockItemEvent> restoreStockItems;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RestoreStockItemEvent {
        private Long productId;
        private Long productVariantId;
        private Integer quantity;
    }
}

