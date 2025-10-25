package com.ecommerce.chat.event;

import com.ecommerce.library.utils.FnCommon;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

import java.util.Objects;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class WebSocketListener {

    private final StringRedisTemplate stringRedisTemplate;

    @EventListener
    public void onSubscribe(SessionSubscribeEvent sessionSubscribeEvent) {
        StompHeaderAccessor stompHeaderAccessor = StompHeaderAccessor.wrap(sessionSubscribeEvent.getMessage());
        String userId = Objects.requireNonNull(stompHeaderAccessor.getUser()).getName();
        String destination = stompHeaderAccessor.getDestination();
        if (FnCommon.isNotNull(destination) && destination.startsWith("/topic/group/")) {
            String groupId = destination.substring("/topic/group/".length());
            stringRedisTemplate.opsForSet().add("group:" + groupId, userId);
            stringRedisTemplate.opsForSet().add("user:" + userId, groupId);

        }
    }

    @EventListener
    public void onUnsubscribe(SessionUnsubscribeEvent sessionUnsubscribeEvent) {
        StompHeaderAccessor stompHeaderAccessor = StompHeaderAccessor.wrap(sessionUnsubscribeEvent.getMessage());
        String userId = Objects.requireNonNull(stompHeaderAccessor.getUser()).getName();
        String destination = stompHeaderAccessor.getDestination();
        if (FnCommon.isNotNull(destination) && destination.startsWith("/topic/group/")) {
            String groupId = destination.substring("/topic/group/".length());
            stringRedisTemplate.opsForSet().remove("user:" + userId, groupId);
            stringRedisTemplate.opsForSet().remove("group:" + groupId, userId);
        }
    }

    @EventListener
    public void onDisconnect(SessionDisconnectEvent sessionDisconnectEvent) {
        StompHeaderAccessor stompHeaderAccessor = StompHeaderAccessor.wrap(sessionDisconnectEvent.getMessage());
        String userId = Objects.requireNonNull(stompHeaderAccessor.getUser()).getName();
        Set<String> groupIds = stringRedisTemplate.opsForSet().members("user:" + userId);
        if (FnCommon.isNotNull(groupIds)) {
            for (String groupId : groupIds) {
                stringRedisTemplate.opsForSet().remove("group:" + groupId, userId);
            }
        }
        stringRedisTemplate.delete("user:" + userId);
    }
}
