package com.ecommerce.order.dto;

import com.ecommerce.library.utils.MessageError;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ResCreateOrderItemDTO {

    @NotNull(message = MessageError.SHOP_NOT_NULL)
    @Schema(description = "Shop ID", example = "1")
    private Long shopId;

    private Long cartItemId;

    private String note;

    @Valid
    @NotNull(message = MessageError.PRODUCT_ORDER_ITEMS_NOT_NULL)
    @Schema(description = "List of product order items")
    private List<ResCreateProductOrderItemDTO> productOrderItems;

}

