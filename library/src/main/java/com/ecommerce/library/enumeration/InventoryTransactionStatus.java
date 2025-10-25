package com.ecommerce.library.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum InventoryTransactionStatus {
    IN("IN"),      // Nhập kho
    OUT("OUT"),     // Xuất kho
    LOCK("LOCK"),    // Khóa hàng cho đơn
    UNLOCK("UNLOCK");   // Mở khóa hàng

    private final String value;
}

