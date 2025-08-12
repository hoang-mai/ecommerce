package com.example.edu.school.apigateway.component;

import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
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
                    if(authentication.getPrincipal() instanceof JwtAuthenticationToken jwtAuthenticationToken){
                        Jwt jwt = jwtAuthenticationToken.getToken();
                        String userId = jwt.getClaimAsString("user-id");
                        String role = jwt.getClaimAsString("role");
                        String email = jwt.getClaimAsString("email");
                        ServerHttpRequest request = exchange.getRequest().mutate()
                                .header("user-id", userId)
                                .header("role", role)
                                .header("email", email)
                                .build();
                        return chain.filter(exchange.mutate().request(request).build());
                    }
                    return chain.filter(exchange);
                });
    }

}