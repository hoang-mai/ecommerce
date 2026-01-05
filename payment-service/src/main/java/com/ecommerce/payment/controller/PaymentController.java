package com.ecommerce.payment.controller;

import com.ecommerce.library.component.MessageService;
import com.ecommerce.library.enumeration.PaymentStatus;
import com.ecommerce.library.utils.BaseResponse;
import com.ecommerce.library.utils.Constant;
import com.ecommerce.library.utils.MessageSuccess;
import com.ecommerce.payment.dto.UpdatePaymentStatusDTO;
import com.ecommerce.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(Constant.PAYMENT)
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;
    private final MessageService messageService;
    @GetMapping("IPN")
    public void ipn(
        @RequestParam(value = "vnp_Amount") String amount,
        @RequestParam(value = "vnp_BankCode") String bankCode,
        @RequestParam(value = "vnp_BankTranNo", required = false) String bankTranNo,
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

    @PatchMapping("/{orderId}")
    public ResponseEntity<BaseResponse<Void>> cancelledOrRefundPayment(@PathVariable Long orderId,
                                                            @RequestBody UpdatePaymentStatusDTO updatePaymentStatusDTO) {
        paymentService.cancelledOrRefundPayment(orderId, updatePaymentStatusDTO.getReason(), updatePaymentStatusDTO.getPaymentStatus());
        return ResponseEntity.ok(
            BaseResponse.<Void>builder()
                .message(messageService.getMessage(PaymentStatus.CANCELLED == updatePaymentStatusDTO.getPaymentStatus() ? MessageSuccess.CANCELLED_SUCCESS : MessageSuccess.REFUND_SUCCESS))
                .statusCode(200)
                .build()
        );
    }
}
