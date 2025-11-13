package com.ecommerce.order.dto;

import com.ecommerce.library.utils.MessageError;
import com.ecommerce.library.utils.validate.PhoneNumberFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ResCreateOrderDTO {

    @NotBlank(message = MessageError.ADDRESS_NOT_BLANK)
    @Schema(description = "Shipping address ID", example = "123e4567-e89b-12d3-a456-426614174000")
    private String address;

    @PhoneNumberFormat
    @NotBlank(message = MessageError.PHONE_NUMBER_NOT_BLANK)
    @Schema(description = "Contact phone number for the order", example = "+1234567890")
    private String phoneNumber;

    @Valid
    @NotEmpty(message = MessageError.ORDER_ITEMS_NOT_EMPTY)
    @Schema(description = "List of items to order")
    private List<ResCreateOrderItemDTO> items;
}

