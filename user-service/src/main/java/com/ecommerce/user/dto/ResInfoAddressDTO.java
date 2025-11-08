package com.ecommerce.user.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ResInfoAddressDTO {

    private Long addressId;
    private String receiverName;
    private String phoneNumber;
    private String province;
    private String ward;
    private String detail;
    private Boolean isDefault;
}
