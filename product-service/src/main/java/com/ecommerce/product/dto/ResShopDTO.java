package com.ecommerce.product.dto;

import com.ecommerce.library.enumeration.ShopStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Builder
public class ResShopDTO {

    private Long shopId;
    private Long ownerId;
    private String shopName;
    private String description;
    private String logoUrl;
    private String bannerUrl;
    private Double rating;
    private ShopStatus shopStatus;
    private String province;
    private String ward;
    private String detail;
    private String phoneNumber;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

