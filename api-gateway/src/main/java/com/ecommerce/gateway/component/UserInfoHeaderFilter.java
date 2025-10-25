package com.ecommerce.gateway.component;

import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class UserInfoHeaderFilter implements GlobalFilter  {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return exchange.getPrincipal().cast(Authentication.class)
                .flatMap(authentication ->{
                    if(authentication.getPrincipal() instanceof Jwt jwt){
                        String userId = jwt.getClaimAsString("user-id");
                        String role = jwt.getClaimAsString("role");
                        ServerHttpRequest request = exchange.getRequest().mutate()
                                .header("user-id", userId)
                                .header("role", role)
                                .build();
                        return chain.filter(exchange.mutate().request(request).build());
                    }
                    return chain.filter(exchange);
                });
    }

}