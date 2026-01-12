package com.ecommerce.flash.sale.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReqCreateFlashSaleProductDTO {
    private Long shopId;

    @NotNull(message = "Campaign ID không được để trống")
    private Long campaignId;

    @NotNull(message = "Product ID không được để trống")
    private Long productId;

    @NotNull(message = "Product Variant ID không được để trống")
    private Long productVariantId;

    @NotNull(message = "Giá gốc không được để trống")
    @DecimalMin(value = "0.0", inclusive = false, message = "Giá gốc phải lớn hơn 0")
    private BigDecimal originalPrice;

    @NotNull(message = "Tổng số lượng không được để trống")
    @Min(value = 1, message = "Tổng số lượng phải lớn hơn 0")
    private Integer totalQuantity;

    @NotNull(message = "Số lượng tối đa mỗi người không được để trống")
    @Min(value = 1, message = "Số lượng tối đa mỗi người phải lớn hơn 0")
    private Integer maxQuantityPerUser;

    @DecimalMin(value = "0.0", message = "Rating phải từ 0 đến 5")
    @DecimalMax(value = "5.0", message = "Rating phải từ 0 đến 5")
    private Double rating;

    @Min(value = 0, message = "Số lượng đã bán phải lớn hơn hoặc bằng 0")
    private Integer totalSold;

    private Double discountPercentage;
}

