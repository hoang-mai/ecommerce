package com.ecommerce.library.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ProductVariantStatus {
    ACTIVE("ACTIVE"),
    INACTIVE("INACTIVE"),
    OUT_OF_STOCK("OUT_OF_STOCK");
    private final String value;
}
