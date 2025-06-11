package com.example.edu.school.apigateway.component;

import com.example.edu.school.apigateway.dto.BaseResponse;

import com.example.edu.school.apigateway.service.JwtService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;

import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.io.IOException;


@Component
@RequiredArgsConstructor
public class JwtFilter implements GlobalFilter, Ordered  {

    private final JwtService jwtService;
    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public int getOrder() {
        return -1;
    }



    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getPath().value();
        if (isPassTokenFilter(path)) {
            return chain.filter(exchange);
        }
        String token = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (token == null || !token.startsWith("Bearer ")) {
            return setErrorResponse(exchange, HttpStatus.NOT_FOUND, "Không có token");
        }
        token = token.substring(7);

        if(!Boolean.TRUE.equals(redisTemplate.hasKey(token))){
            return setErrorResponse(exchange, HttpStatus.UNAUTHORIZED, "Token đã hết hạn");
        }
        try {
            if(!jwtService.validateToken(token)){
                return  setErrorResponse(exchange, HttpStatus.UNAUTHORIZED, "Token không hợp lệ");

            }
            String email = jwtService.extractEmail(token);
            ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
                    .header("X-Email", email)
                    .build();

            return chain.filter(exchange.mutate().request(modifiedRequest).build());
        }catch (Exception e){
            return  setErrorResponse(exchange, HttpStatus.INTERNAL_SERVER_ERROR, "Lỗi không xác định");
        } finally {
            redisTemplate.delete(token);
        }
    }

    private boolean isPassTokenFilter(String path) {
        return path.startsWith("/api/auth/login") || path.startsWith("/api/auth/register");
    }
    private Mono<Void> setErrorResponse(ServerWebExchange exchange,  HttpStatus httpStatus ,String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        response.setStatusCode(httpStatus);
        BaseResponse<Void> errorResponse = BaseResponse.<Void>builder()
                .statusCode(httpStatus.value())
                .message(message)
                .build();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            byte[] bytes = objectMapper.writeValueAsBytes(errorResponse);
            DataBuffer buffer = response.bufferFactory().wrap(bytes);
            return response.writeWith(Mono.just(buffer));
        } catch (JsonProcessingException e) {
            response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
            return response.setComplete();
        }
    }


}