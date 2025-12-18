package com.ecommerce.payment.controller;

import com.ecommerce.library.utils.Constant;
import com.ecommerce.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Constant.PAYMENT)
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;
    @GetMapping("IPN")
    public void ipn(
        @RequestParam(value = "vnp_Amount") String amount,
        @RequestParam(value = "vnp_BankCode") String bankCode,
        @RequestParam(value = "vnp_BankTranNo") String bankTranNo,
        @RequestParam(value = "vnp_CardType") String cardType,
        @RequestParam(value = "vnp_OrderInfo") String orderInfo,
        @RequestParam(value = "vnp_PayDate") String payDate,
        @RequestParam(value = "vnp_ResponseCode") String responseCode,
        @RequestParam(value = "vnp_TmnCode") String tmnCode,
        @RequestParam(value = "vnp_TransactionNo") String transactionNo,
        @RequestParam(value = "vnp_TransactionStatus") String transactionStatus,
        @RequestParam(value = "vnp_TxnRef") String txnRef,
        @RequestParam(value = "vnp_SecureHash") String secureHash
    ){
        paymentService.handleIPN(amount, bankCode, bankTranNo, cardType, orderInfo, payDate, responseCode, tmnCode, transactionNo, transactionStatus, txnRef, secureHash);
    }
}
