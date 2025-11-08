package com.ecommerce.library.enumeration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CategoryStatus {
    ACTIVE("ACTIVE"),
    INACTIVE("INACTIVE");
    private final String value;
}
