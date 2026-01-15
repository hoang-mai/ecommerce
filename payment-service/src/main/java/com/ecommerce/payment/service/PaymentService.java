package com.ecommerce.payment.service;

import com.ecommerce.library.enumeration.PaymentStatus;
import com.ecommerce.library.kafka.event.order.CreateListOrderEvent;

public interface PaymentService {
    void handleCreatePaymentEvent(CreateListOrderEvent createListOrderEvent);

    void handleIPN(String amount, String bankCode, String bankTranNo, String cardType, String orderInfo, String payDate,
            String responseCode, String tmnCode, String transactionNo, String transactionStatus, String txnRef,
            String secureHash);

    void cancelledOrRefundPayment(Long orderId, String reason, PaymentStatus paymentStatus);
}
