package com.ecommerce.product.dto;

import com.ecommerce.library.enumeration.ShopStatus;
import com.ecommerce.library.utils.MessageError;
import com.ecommerce.library.utils.validate.PhoneNumberFormat;
import com.ecommerce.library.utils.validate.StatusFormat;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

import java.util.Set;

@Getter
public class ReqUpdateShopDTO {


    @NotBlank(message = MessageError.SHOP_NAME_NOT_BLANK)
    private String shopName;
    private String description;
    private String logoUrl;
    private String bannerUrl;

    @NotBlank(message = MessageError.PROVINCE_NOT_BLANK)
    private String province;
    @NotBlank(message = MessageError.WARD_NOT_BLANK)
    private String ward;
    @NotBlank(message = MessageError.DETAIL_NOT_BLANK)
    private String detail;

    @PhoneNumberFormat
    @NotBlank(message = MessageError.PHONE_NUMBER_NOT_BLANK)
    private String phoneNumber;

    private Set<Long> categoryIds;
}
