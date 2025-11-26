package com.ecommerce.library.kafka.event.shop;

import com.ecommerce.library.enumeration.ShopStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateShopStatusEvent {
    private Long shopId;
    private ShopStatus shopStatus;
}
