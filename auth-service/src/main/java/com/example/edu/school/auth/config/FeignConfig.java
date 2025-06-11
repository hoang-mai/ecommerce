package com.example.edu.school.auth.config;

import com.example.edu.school.auth.dto.response.BaseResponse;
import com.example.edu.school.auth.exception.EmailNotFoundException;
import com.example.edu.school.auth.exception.ExceptionDetail;
import com.example.edu.school.auth.exception.FeignException;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import io.lettuce.core.ClientOptions;
import org.springframework.boot.autoconfigure.data.redis.LettuceClientConfigurationBuilderCustomizer;
import org.springframework.boot.ssl.SslOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import feign.Logger;
import feign.codec.ErrorDecoder;
import feign.codec.ErrorDecoder.Default;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Configuration
public class FeignConfig {
    @Bean
    public LettuceClientConfigurationBuilderCustomizer lettuceClientConfigurationBuilderCustomizer() {
        return LettuceClientConfiguration.LettuceClientConfigurationBuilder::useSsl;
    }

    @Bean
    public ErrorDecoder errorDecoder() {
        return new ErrorDecoder() {
            private final ObjectMapper objectMapper = new ObjectMapper();
            private String getResponseBody(Response response) {
                try (InputStream inputStream = response.body().asInputStream()) {
                    return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                } catch (IOException e) {
                    throw new RuntimeException("Không thể đọc phản hồi từ máy chủ", e);
                }
            }
            private BaseResponse<ExceptionDetail> parseErrorResponse(String responseBody) {
                try {
                    objectMapper.findAndRegisterModules();
                    return objectMapper.readValue(responseBody,
                            objectMapper.getTypeFactory().constructParametricType(BaseResponse.class, ExceptionDetail.class));
                } catch (IOException e) {
                    throw new RuntimeException("Không thể phân tích cú pháp phản hồi lỗi", e);
                }
            }
            @Override
            public Exception decode(String s, Response response) {
                String responseBody = getResponseBody(response);

                BaseResponse<ExceptionDetail> errorResponse = parseErrorResponse(responseBody);

                return new FeignException(errorResponse.getMessage(), errorResponse);
            }
        };
    }

}
