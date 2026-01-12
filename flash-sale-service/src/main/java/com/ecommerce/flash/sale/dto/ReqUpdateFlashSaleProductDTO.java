package com.ecommerce.flash.sale.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReqUpdateFlashSaleProductDTO {

    private BigDecimal originalPrice;

    @NotNull(message = "Phần trăm giảm giá không được để trống")
    private Double discountPercentage;

    @NotNull(message = "Tổng số lượng không được để trống")
    @Min(value = 1, message = "Tổng số lượng phải lớn hơn 0")
    private Integer totalQuantity;

    @NotNull(message = "Số lượng tối đa mỗi người không được để trống")
    @Min(value = 1, message = "Số lượng tối đa mỗi người phải lớn hơn 0")
    private Integer maxQuantityPerUser;

    private Double rating;

    private Integer totalSold;
}

