package com.ecommerce.order.dto;

import com.ecommerce.library.enumeration.OrderStatus;
import com.ecommerce.library.utils.MessageError;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ReqUpdateOrderStatus {

    @NotNull(message = MessageError.ORDER_STATUS_NOT_NULL)
    private OrderStatus orderStatus;


    private String reason;

}
