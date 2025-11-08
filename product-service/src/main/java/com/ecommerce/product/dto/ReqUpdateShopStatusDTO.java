package com.ecommerce.product.dto;

import com.ecommerce.library.enumeration.ShopStatus;
import com.ecommerce.library.utils.MessageError;
import com.ecommerce.library.utils.validate.StatusFormat;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class ReqUpdateShopStatusDTO {

    @NotNull(message = MessageError.STATUS_NOT_NULL)
    @StatusFormat(acceptedValues = {ShopStatus.ACTIVE, ShopStatus.INACTIVE, ShopStatus.SUSPENDED})
    private ShopStatus status;
}

