package com.ecommerce.chat.notification.listener;

import com.ecommerce.library.utils.FnCommon;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.security.Principal;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.ecommerce.library.utils.Constant.TTL_SECONDS;

@Component
@RequiredArgsConstructor
public class WebSocketListener {

    private final RedisTemplate<String, String> redisTemplate;

    @EventListener
    public void onSubscribe(SessionConnectedEvent sessionConnectedEvent) {
        if (!FnCommon.isNotNull(sessionConnectedEvent)) {
            return;
        }

        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(sessionConnectedEvent.getMessage());

        String sessionId = accessor.getSessionId();

        String userId = null;
        Principal principal = accessor.getUser();
        if (FnCommon.isNotNull(principal)) {
            userId = principal.getName();
        } else {
            List<String> nativeUserIds = accessor.getNativeHeader("userId");
            if (FnCommon.isNotNullOrEmptyList(nativeUserIds)) {
                userId = nativeUserIds.get(0);
            }
        }

        if (!FnCommon.isNotNullOrEmpty(sessionId) || !FnCommon.isNotNullOrEmpty(userId)) {
            return;
        }

        try {
            String sessionKey = "chat:session:" + sessionId;
            String userKey = "chat:user:" + userId;


            redisTemplate.opsForValue().set(sessionKey, userId, TTL_SECONDS, TimeUnit.SECONDS);
            redisTemplate.opsForValue().set(userKey, sessionId, TTL_SECONDS, TimeUnit.SECONDS);
        } catch (Exception ignored) {
        }
    }

    @EventListener
    public void onDisconnect(SessionDisconnectEvent sessionDisconnectEvent) {
        if (!FnCommon.isNotNull(sessionDisconnectEvent)) {
            return;
        }

        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(sessionDisconnectEvent.getMessage());

        String sessionId = accessor.getSessionId();
        if (!FnCommon.isNotNullOrEmpty(sessionId)) {
            return;
        }

        try {
            String sessionKey = "chat:session:" + sessionId;
            String userId = redisTemplate.opsForValue().get(sessionKey);
            if (FnCommon.isNotNullOrEmpty(userId)) {
                String userKey = "chat:user:" + userId;
                redisTemplate.delete(userKey);
            }
            redisTemplate.delete(sessionKey);
        } catch (Exception ignored) {
        }
    }

}