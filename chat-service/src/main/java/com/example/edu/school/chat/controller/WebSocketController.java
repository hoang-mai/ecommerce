package com.example.edu.school.chat.controller;


import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class WebSocketController {

    private final SimpMessagingTemplate simpMessagingTemplate;

    @MessageMapping("/hello")
    @SendTo("/topic/greetings")
    public String greeting(String message) throws Exception {
        // Simulate processing the message
        Thread.sleep(1000); // simulated delay
        return  message ;
    }

    @MessageMapping("/private")
    public void sendPrivateMessage(@Payload PrivateMessage message) {
        // Gửi đến user cụ thể (theo receiver)
        System.out.println("Sending private message: " + message);
        System.out.println("Receiver: " + message.getReceiver());
        simpMessagingTemplate.convertAndSendToUser(
            message.getReceiver(), "/queue/messages", message
        );
        
    }
}
@Data
class PrivateMessage {
    private String sender;
    private String receiver;
    private String content;
}