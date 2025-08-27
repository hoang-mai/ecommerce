package com.example.app.chat.chat.event;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

@Component
@RequiredArgsConstructor
public class WebSocketListener {

    private final RedisTemplate<String, String> redisTemplate;

    @EventListener
    public void onSubscribe(SessionSubscribeEvent sessionSubscribeEvent) {

    }
}
