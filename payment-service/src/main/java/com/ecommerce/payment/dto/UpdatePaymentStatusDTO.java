package com.ecommerce.payment.dto;

import com.ecommerce.library.enumeration.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UpdatePaymentStatusDTO {
    private PaymentStatus paymentStatus;
    private String reason;
}
