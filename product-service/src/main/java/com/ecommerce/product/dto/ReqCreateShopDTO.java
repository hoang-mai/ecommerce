package com.ecommerce.product.dto;

import com.ecommerce.library.utils.MessageError;
import com.ecommerce.library.utils.validate.PhoneNumberFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;

@Getter
public class ReqCreateShopDTO {


    @NotBlank(message = MessageError.SHOP_NAME_NOT_BLANK)
    private String shopName;

    private String description;

    @NotBlank(message = MessageError.PROVINCE_NOT_BLANK)
    private String province;
    @NotBlank(message = MessageError.WARD_NOT_BLANK)
    private String ward;
    @NotBlank(message = MessageError.DETAIL_NOT_BLANK)
    private String detail;

    @PhoneNumberFormat
    @NotBlank(message = MessageError.PHONE_NUMBER_NOT_BLANK)
    private String phoneNumber;
}
