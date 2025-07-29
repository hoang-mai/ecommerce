package com.example.edu.school.auth.config;


import com.example.edu.school.library.component.MessageService;
import com.example.edu.school.library.exception.HttpRequestException;
import com.example.edu.school.library.utils.BaseResponse;
import com.example.edu.school.library.utils.MessageError;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import feign.codec.ErrorDecoder;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Configuration
@RequiredArgsConstructor
public class FeignClientConfig {

    private final MessageService messageService;

    @Bean
    public ErrorDecoder errorDecoder() {
        return new ErrorDecoder() {
            private final ObjectMapper objectMapper = new ObjectMapper();
            private String getResponseBody(Response response) {
                try (InputStream inputStream = response.body().asInputStream()) {
                    return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                } catch (IOException e) {
                    throw new RuntimeException(messageService.getMessage(MessageError.CANNOT_READ_RESPONSE_FROM_SERVER), e);
                }
            }
            private BaseResponse<Void> parseErrorResponse(String responseBody) {
                try {
                    objectMapper.findAndRegisterModules();
                    return objectMapper.readValue(responseBody,
                            objectMapper.getTypeFactory().constructParametricType(BaseResponse.class, Void.class));
                } catch (IOException e) {
                    throw new RuntimeException(messageService.getMessage(MessageError.CANNOT_PARSE_ERROR_RESPONSE), e);
                }
            }
            @Override
            public Exception decode(String s, Response response) {
                String responseBody = getResponseBody(response);

                BaseResponse<Void> errorResponse = parseErrorResponse(responseBody);

                return new HttpRequestException(errorResponse.getMessage(), errorResponse.getStatusCode(), errorResponse.getTimestamp());
            }
        };
    }

}