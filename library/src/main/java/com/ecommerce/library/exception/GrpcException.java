package com.ecommerce.library.exception;

import com.ecommerce.library.component.MessageService;
import io.grpc.Metadata;
import io.grpc.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.grpc.server.exception.GrpcExceptionHandler;

import java.time.LocalDateTime;

@Configuration
@RequiredArgsConstructor
public class GrpcException {

    private final MessageService messageService;

    @Bean
    public GrpcExceptionHandler grpcExceptionHandler() {
        Metadata metadata = new Metadata();
        metadata.put(Metadata.Key.of("timestamp", Metadata.ASCII_STRING_MARSHALLER), String.valueOf(LocalDateTime.now()));
        return exception -> {
            if (exception instanceof HttpRequestException) {
                return Status.INTERNAL
                        .withDescription(messageService.getMessage(exception.getMessage()))
                        .asException(metadata);
            } else if (exception instanceof DuplicateException) {
                return Status.ALREADY_EXISTS
                        .withDescription(messageService.getMessage(exception.getMessage()))
                        .asException(metadata);
            } else if (exception instanceof NotFoundException) {
                return Status.NOT_FOUND
                        .withDescription(messageService.getMessage(exception.getMessage()))
                        .asException(metadata);
            }
            return null;
        };
    }
}
