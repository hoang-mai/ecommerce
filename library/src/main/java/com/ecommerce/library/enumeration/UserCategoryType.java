package com.ecommerce.library.enumeration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserCategoryType {
    CLICK("Click", 1.0),
    ADD_TO_CART("Add to Cart", 3.0),
    REMOVE_FROM_CART("Remove from Cart", -2.0),
    PURCHASE("Purchase", 5.0);

    private final String type;
    private final Double weight;
}
