package com.ecommerce.payment.service.impl;

import com.ecommerce.library.enumeration.OrderStatus;
import com.ecommerce.library.enumeration.PaymentStatus;
import com.ecommerce.library.exception.NotFoundException;
import com.ecommerce.library.kafka.event.order.CreateListOrderEvent;
import com.ecommerce.library.kafka.event.flashsale.RestoreFlashSaleStockEvent;
import com.ecommerce.library.kafka.event.order.CreateListOrderStatusEvent;
import com.ecommerce.library.kafka.event.order.OrderStatusEvent;
import com.ecommerce.library.kafka.event.payment.CreatePaymentEvent;
import com.ecommerce.library.kafka.event.product.RestoreStockEvent;
import com.ecommerce.library.utils.FnCommon;
import com.ecommerce.library.utils.MessageError;
import com.ecommerce.payment.dto.VnpayResponseDTO;
import com.ecommerce.payment.entity.OrderCache;
import com.ecommerce.payment.entity.OrderItemCache;
import com.ecommerce.payment.entity.Payment;
import com.ecommerce.payment.messaging.producer.OrderEventProducer;
import com.ecommerce.payment.repository.OrderCacheRepository;
import com.ecommerce.payment.repository.PaymentRepository;
import com.ecommerce.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

        private final OrderEventProducer orderEventProducer;

        private final PaymentRepository paymentRepository;
        private final String vnp_Version = "2.1.0";
        private final OrderCacheRepository orderCacheRepository;
        @Value("${vnpay.vnp_TmnCode}")
        private String vnp_TmnCode;
        @Value("${vnpay.vnp_HashSecret}")
        private String vnp_HashSecret;
        private final String orderType = "other";

        private final String vnp_CurrCode = "VND";
        private final String vnp_Locale = "vn";
        @Value("${vnpay.vnp_ReturnUrl}")
        private String vnp_ReturnUrl;
        private final String vnp_IpAddr = "127.0.0.1";

        private final String vnp_PayUrl = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html";
        public final String vnp_ApiUrl = "https://sandbox.vnpayment.vn/merchant_webapi/api/transaction";

        @Override
        public void handleCreatePaymentEvent(CreateListOrderEvent createListOrderEvent) {
                AtomicBoolean isPartiallyOutOfStock = new AtomicBoolean(false);
                boolean isAllOutOfStock = false;
                Payment payment = Payment.builder()
                                .paymentStatus(PaymentStatus.PENDING)
                                .paymentCode(generatePaymentCode())
                                .userId(createListOrderEvent.getUserId())
                                .build();
                paymentRepository.save(payment);
                createListOrderEvent.getCreateOrderEventList().forEach(createOrderEvent -> {
                        if (createOrderEvent.getOrderStatus() == OrderStatus.CANCELLED) {
                                isPartiallyOutOfStock.set(true);
                                return;
                        }

                        payment.addPrice(createOrderEvent.getTotalPrice());
                        OrderCache orderCache = OrderCache.builder()
                                        .orderId(createOrderEvent.getOrderId())
                                        .ownerId(createOrderEvent.getOwnerId())
                                        .shopId(createOrderEvent.getShopId())
                                        .totalPrice(createOrderEvent.getTotalPrice())
                                        .build();
                        createOrderEvent.getCreateOrderItemEventList().forEach(orderItemEvent -> {
                                orderCache.addOrderItemCache(
                                                OrderItemCache.builder()
                                                                .orderItemId(orderItemEvent.getOrderItemId())
                                                                .productId(orderItemEvent.getProductId())
                                                                .quantity(orderItemEvent.getQuantity())
                                                                .productVariantId(orderItemEvent.getProductVariantId())
                                                                .quantityDiscount(orderItemEvent.getQuantityDiscount())
                                                                .flashSaleProductId(
                                                                                orderItemEvent.getFlashSaleProductId())
                                                    .totalDiscount(orderItemEvent.getTotalDiscount())
                                                    .totalFinalPrice(orderItemEvent.getTotalFinalPrice())
                                                    .totalPrice(orderItemEvent.getTotalPrice())
                                                                .build());
                        });
                        payment.addOrderCache(orderCache);
                });
                String amountStr = payment.getPrice()
                                .multiply(BigDecimal.valueOf(100))
                                .toPlainString();

                if (amountStr.contains(".")) {
                        amountStr = amountStr.substring(0, amountStr.indexOf('.'));
                }

                Map<String, String> vnp_Params = new HashMap<>();
                vnp_Params.put("vnp_Version", vnp_Version);
                String vnp_Command = "pay";
                vnp_Params.put("vnp_Command", vnp_Command);
                vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
                vnp_Params.put("vnp_Amount", amountStr);
                vnp_Params.put("vnp_CreateDate", payment.getCreatedAt().atZone(ZoneId.of("Asia/Ho_Chi_Minh"))
                                .format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));
                vnp_Params.put("vnp_CurrCode", vnp_CurrCode);
                vnp_Params.put("vnp_IpAddr", vnp_IpAddr);
                vnp_Params.put("vnp_Locale", vnp_Locale);
                String vnp_OrderInfo = "Thanh toan don hang";
                vnp_Params.put("vnp_OrderInfo", vnp_OrderInfo);
                vnp_Params.put("vnp_OrderType", orderType);
                vnp_Params.put("vnp_ReturnUrl", vnp_ReturnUrl);
                vnp_Params.put("vnp_ExpireDate",
                                payment.getCreatedAt().atZone(ZoneId.of("Asia/Ho_Chi_Minh")).plusMinutes(15)
                                                .format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));
                vnp_Params.put("vnp_TxnRef", payment.getPaymentCode());

                List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
                Collections.sort(fieldNames);
                StringBuilder hashData = new StringBuilder();
                StringBuilder query = new StringBuilder();
                Iterator<String> itr = fieldNames.iterator();
                while (itr.hasNext()) {
                        String fieldName = itr.next();
                        String fieldValue = vnp_Params.get(fieldName);
                        if (FnCommon.isNotNullOrEmpty(fieldValue)) {
                                hashData.append(fieldName);
                                hashData.append('=');
                                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                                // Build query
                                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII));
                                query.append('=');
                                query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                                if (itr.hasNext()) {
                                        query.append('&');
                                        hashData.append('&');
                                }
                        }
                }
                String queryUrl = query.toString();
                String vnp_SecureHash = hmacSHA512(vnp_HashSecret, hashData.toString());
                queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
                String paymentUrl = vnp_PayUrl + "?" + queryUrl;
                orderEventProducer.send(CreatePaymentEvent.builder()
                                .userId(createListOrderEvent.getUserId())
                                .isPartiallyOutOfStock(isPartiallyOutOfStock.get())
                                .isAllOutOfStock(payment.getPrice().compareTo(BigDecimal.ZERO) == 0)
                                .paymentUrl(paymentUrl)
                                .build());
                paymentRepository.save(payment);

        }

        @Override
        public void handleIPN(String amount, String bankCode, String bankTranNo, String cardType, String orderInfo,
                        String payDate, String responseCode, String tmnCode, String transactionNo,
                        String transactionStatus,
                        String txnRef, String secureHash) {
                Map<String, String> vnp_Params = new HashMap<>();
                vnp_Params.put("vnp_Amount", amount);
                vnp_Params.put("vnp_BankCode", bankCode);
                vnp_Params.put("vnp_BankTranNo", bankTranNo);
                vnp_Params.put("vnp_CardType", cardType);
                vnp_Params.put("vnp_OrderInfo", orderInfo);
                vnp_Params.put("vnp_PayDate", payDate);
                vnp_Params.put("vnp_ResponseCode", responseCode);
                vnp_Params.put("vnp_TmnCode", tmnCode);
                vnp_Params.put("vnp_TransactionNo", transactionNo);
                vnp_Params.put("vnp_TransactionStatus", transactionStatus);
                vnp_Params.put("vnp_TxnRef", txnRef);

                List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
                Collections.sort(fieldNames);
                StringBuilder hashData = new StringBuilder();
                for (String fieldName : fieldNames) {
                        String fieldValue = vnp_Params.get(fieldName);
                        if (FnCommon.isNotNullOrEmpty(fieldValue)) {
                                hashData.append(fieldName);
                                hashData.append('=');
                                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                                hashData.append('&');
                        }
                }
                String hashDataStr = hashData.substring(0, hashData.length() - 1);
                String checkSum = hmacSHA512(vnp_HashSecret, hashDataStr);
                if (checkSum.equals(secureHash)) {
                        Payment payment = paymentRepository.findByPaymentCode(txnRef).orElse(null);
                        if (payment == null)
                                return;
                        if (payment.getPaymentStatus() == PaymentStatus.SUCCESS)
                                return;
                        if ("00".equals(responseCode)) {
                                payment.setPaymentStatus(PaymentStatus.SUCCESS);
                                payment.setTransactionNo(transactionNo);
                                payment.setPayDate(payDate);
                                paymentRepository.save(payment);
                                orderEventProducer.sendUpdatePaymentStatusEvent(
                                                CreateListOrderStatusEvent.builder()
                                                                .userId(payment.getUserId())
                                                                .orderStatusEventList(payment.getOrderCaches().stream()
                                                                                .map(orderCache -> OrderStatusEvent
                                                                                                .builder()
                                                                                                .orderId(orderCache
                                                                                                                .getOrderId())
                                                                                                .ownerId(orderCache
                                                                                                                .getOwnerId())
                                                                                                .orderStatus(OrderStatus.PAID)
                                                                                                .build())
                                                                                .toList())
                                                                .build());
                        } else {
                                payment.setPaymentStatus(PaymentStatus.FAILED);
                                paymentRepository.save(payment);

                                // Send order cancellation event
                                orderEventProducer.sendUpdatePaymentStatusEvent(
                                                CreateListOrderStatusEvent.builder()
                                                                .userId(payment.getUserId())
                                                                .orderStatusEventList(payment.getOrderCaches().stream()
                                                                                .map(orderCache -> OrderStatusEvent
                                                                                                .builder()
                                                                                                .orderId(orderCache
                                                                                                                .getOrderId())
                                                                                                .ownerId(orderCache
                                                                                                                .getOwnerId())
                                                                                                .orderStatus(OrderStatus.CANCELLED)
                                                                                                .reason("Thanh toán không thành công")
                                                                                                .build())
                                                                                .toList())
                                                                .build());

                                // Send restore stock event to product-service
                                List<RestoreStockEvent.RestoreStockItemEvent> restoreStockItems = new ArrayList<>();
                                payment.getOrderCaches().forEach(orderCache -> {
                                        orderCache.getOrderItems().forEach(orderItemCache -> {
                                                restoreStockItems.add(
                                                                RestoreStockEvent.RestoreStockItemEvent.builder()
                                                                                .productId(orderItemCache
                                                                                                .getProductId())
                                                                                .productVariantId(orderItemCache
                                                                                                .getProductVariantId())
                                                                                .quantity(orderItemCache.getQuantity())
                                                                                .build());
                                        });
                                });

                                orderEventProducer.sendRestoreStockEvent(
                                                RestoreStockEvent.builder()
                                                                .userId(payment.getUserId())
                                                                .restoreStockItems(restoreStockItems)
                                                                .build());

                                // Send restore flash sale stock event to flash-sale-service
                                List<RestoreFlashSaleStockEvent.RestoreFlashSaleItemEvent> restoreFlashSaleStockItems = new ArrayList<>();
                                payment.getOrderCaches().forEach(orderCache -> {
                                        orderCache.getOrderItems().forEach(orderItemCache -> {
                                                if (FnCommon.isNotNull(orderItemCache.getFlashSaleProductId())) {
                                                        restoreFlashSaleStockItems.add(
                                                                        RestoreFlashSaleStockEvent.RestoreFlashSaleItemEvent
                                                                                        .builder()
                                                                                        .flashSaleProductId(
                                                                                                        orderItemCache.getFlashSaleProductId())
                                                                                        .quantity(orderItemCache
                                                                                                        .getQuantityDiscount())
                                                                                        .totalFinalPrice(orderItemCache
                                                                                                        .getTotalFinalPrice())
                                                                                        .build());
                                                }
                                        });
                                });

                                if (!restoreFlashSaleStockItems.isEmpty()) {
                                        orderEventProducer.sendRestoreFlashSaleStockEvent(
                                                        RestoreFlashSaleStockEvent.builder()
                                                                        .userId(payment.getUserId())
                                                                        .restoreFlashSaleItems(
                                                                                        restoreFlashSaleStockItems)
                                                                        .build());
                                }
                        }
                }
        }

        @Override
        @Transactional
        public void cancelledOrRefundPayment(Long orderId, String reason, PaymentStatus paymentStatus) {
                OrderCache orderCache = orderCacheRepository.findByOrderId(orderId)
                                .orElseThrow(() -> new NotFoundException(MessageError.ORDER_NOT_FOUND));
                Payment payment = orderCache.getPayment();
                int countOrder = payment.getOrderCaches().size();
                String vnp_TransactionType;
                if (countOrder - 1 == payment.getCountRefund()) {
                        vnp_TransactionType = "02";
                } else {
                        vnp_TransactionType = "03";
                }

                Payment refundPayment = Payment.builder()
                                .paymentStatus(paymentStatus)
                                .paymentCode(generatePaymentCode())
                                .userId(payment.getUserId())
                                .price(orderCache.getTotalPrice())
                                .reason(reason)
                                .build();
                paymentRepository.save(refundPayment);
                String amountStr = refundPayment.getPrice()
                                .multiply(BigDecimal.valueOf(100))
                                .toPlainString();

                if (amountStr.contains(".")) {
                        amountStr = amountStr.substring(0, amountStr.indexOf('.'));
                }
                Map<String, String> vnp_Params = new HashMap<>();
                vnp_Params.put("vnp_RequestId", refundPayment.getPaymentCode());
                vnp_Params.put("vnp_Version", vnp_Version);
                vnp_Params.put("vnp_Command", "refund");
                vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
                vnp_Params.put("vnp_TransactionType", vnp_TransactionType);
                vnp_Params.put("vnp_TxnRef", payment.getPaymentCode());
                vnp_Params.put("vnp_Amount", amountStr);
                vnp_Params.put("vnp_OrderInfo", "Hoan tien don hang");
                vnp_Params.put("vnp_TransactionNo", payment.getTransactionNo());
                vnp_Params.put("vnp_TransactionDate", payment.getPayDate());
                vnp_Params.put("vnp_CreateBy", String.valueOf(payment.getUserId()));
                vnp_Params.put("vnp_CreateDate", refundPayment.getCreatedAt().atZone(ZoneId.of("Asia/Ho_Chi_Minh"))
                                .format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));
                vnp_Params.put("vnp_IpAddr", vnp_IpAddr);
                String hash_Data = String.join("|",
                                vnp_Params.get("vnp_RequestId"),
                                vnp_Params.get("vnp_Version"),
                                vnp_Params.get("vnp_Command"),
                                vnp_Params.get("vnp_TmnCode"),
                                vnp_Params.get("vnp_TransactionType"),
                                vnp_Params.get("vnp_TxnRef"),
                                vnp_Params.get("vnp_Amount"),
                                vnp_Params.get("vnp_TransactionNo"),
                                vnp_Params.get("vnp_TransactionDate"),
                                vnp_Params.get("vnp_CreateBy"),
                                vnp_Params.get("vnp_CreateDate"),
                                vnp_Params.get("vnp_IpAddr"),
                                vnp_Params.get("vnp_OrderInfo"));
                vnp_Params.put("vnp_SecureHash", hmacSHA512(vnp_HashSecret, hash_Data));

                VnpayResponseDTO vnpayResponseDTO = RestClient.builder()
                                .baseUrl(vnp_ApiUrl)
                                .defaultHeader("Accept", "application/json")
                                .build()
                                .post()
                                .body(vnp_Params)
                                .retrieve()
                                .body(VnpayResponseDTO.class);

                payment.setCountRefund(payment.getCountRefund() + 1);
                paymentRepository.save(payment);
                orderEventProducer.sendUpdatePaymentStatusEvent(
                                CreateListOrderStatusEvent.builder()
                                                .userId(payment.getUserId())
                                                .orderStatusEventList(List.of(
                                                                OrderStatusEvent.builder()
                                                                                .orderId(orderCache.getOrderId())
                                                                                .ownerId(orderCache.getOwnerId())
                                                                                .orderStatus(
                                                                                                PaymentStatus.CANCELLED == paymentStatus
                                                                                                                ? OrderStatus.CANCELLED
                                                                                                                : OrderStatus.RETURNED)
                                                                                .reason(reason)
                                                                                .build()))
                                                .build());
                orderEventProducer.sendRestoreStockEvent(
                                RestoreStockEvent.builder()
                                                .userId(payment.getUserId())
                                                .restoreStockItems(orderCache.getOrderItems().stream()
                                                                .map(orderItemCache -> RestoreStockEvent.RestoreStockItemEvent
                                                                                .builder()
                                                                                .productId(orderItemCache
                                                                                                .getProductId())
                                                                                .productVariantId(orderItemCache
                                                                                                .getProductVariantId())
                                                                                .quantity(orderItemCache.getQuantity())
                                                                                .build())
                                                                .toList())
                                                .build());

                // Send restore flash sale stock event to flash-sale-service
                List<RestoreFlashSaleStockEvent.RestoreFlashSaleItemEvent> restoreFlashSaleStockItems = new ArrayList<>();
                orderCache.getOrderItems().forEach(orderItemCache -> {
                        if (FnCommon.isNotNull(orderItemCache.getFlashSaleProductId())) {
                                restoreFlashSaleStockItems.add(
                                                RestoreFlashSaleStockEvent.RestoreFlashSaleItemEvent.builder()
                                                                .flashSaleProductId(
                                                                                orderItemCache.getFlashSaleProductId())
                                                                .quantity(orderItemCache.getQuantityDiscount())
                                                                .totalFinalPrice(orderItemCache.getTotalFinalPrice())
                                                                .build());
                        }
                });

                if (!restoreFlashSaleStockItems.isEmpty()) {
                        orderEventProducer.sendRestoreFlashSaleStockEvent(
                                        RestoreFlashSaleStockEvent.builder()
                                                        .userId(payment.getUserId())
                                                        .restoreFlashSaleItems(restoreFlashSaleStockItems)
                                                        .build());
                }
        }

        private String hmacSHA512(String key, String data) {
                try {
                        Mac hmac512 = Mac.getInstance("HmacSHA512");
                        byte[] hmacKeyBytes = key.getBytes();
                        SecretKeySpec secretKey = new SecretKeySpec(hmacKeyBytes, "HmacSHA512");
                        hmac512.init(secretKey);
                        byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
                        byte[] result = hmac512.doFinal(dataBytes);
                        StringBuilder sb = new StringBuilder(2 * result.length);
                        for (byte b : result) {
                                sb.append(String.format("%02x", b & 0xff));
                        }
                        return sb.toString();
                } catch (Exception ex) {
                        return "";
                }
        }

        private String generatePaymentCode() {
                Instant instant = Instant.now();
                LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.of("Asia/Ho_Chi_Minh"));
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
                String random = RandomStringUtils.secure().next(4, "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ");
                return "PAY" + localDateTime.format(formatter) + random;
        }
}
