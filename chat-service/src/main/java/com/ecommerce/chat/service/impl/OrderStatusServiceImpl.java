package com.ecommerce.chat.service.impl;

import com.ecommerce.chat.service.OrderStatusService;
import com.ecommerce.library.kafka.event.order.OderStatusEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderStatusServiceImpl implements OrderStatusService {

    private final SimpMessagingTemplate simpMessagingTemplate;


    @Override
    public void sendOrderStatusMessage(OderStatusEvent oderStatusEvent) {
        simpMessagingTemplate.convertAndSendToUser(
                oderStatusEvent.getUserId().toString(),
                "/queue/order-status",
                oderStatusEvent
        );
    }
}
