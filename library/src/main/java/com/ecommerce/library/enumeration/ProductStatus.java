package com.ecommerce.library.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ProductStatus {
    ACTIVE("ACTIVE"),
    INACTIVE("INACTIVE");
    private final String value;
}
