package com.ecommerce.library.kafka.event.product;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateFlashSaleProductStockEvent {
    private Long flashSaleProductId;
    private Integer stock;
}
