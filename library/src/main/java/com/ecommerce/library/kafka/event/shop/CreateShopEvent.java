package com.ecommerce.library.kafka.event.shop;

import com.ecommerce.library.enumeration.ShopStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CreateShopEvent {

    private Long shopId;
    private Long ownerId;
    private String shopName;
    private String description;
    private String logoUrl;
    private String bannerUrl;
    private ShopStatus shopStatus;
    private String province;
    private String ward;
    private String detail;
    private String phoneNumber;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
