package com.ecommerce.library.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum OrderStatus {
    PENDING("PENDING", "Đang chờ"),
    CONFIRMED("CONFIRMED", "Đã xác nhận"),
    PAID("PAID", "Đã thanh toán"),
    DELIVERED("DELIVERED", "Đang vận chuyển"),
    SHIPPED("SHIPPED", "Đang giao"),
    COMPLETED("COMPLETED", "Hoàn thành"),
    RETURNED("RETURNED", "Đã trả hàng"),
    CANCELLED("CANCELLED", "Đã hủy");

    private final String value;
    private final String valueVi;
}