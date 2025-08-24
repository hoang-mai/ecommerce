package com.example.app.chat.chat.exception;

import com.example.app.chat.library.component.MessageService;
import com.example.app.chat.library.exception.HttpRequestException;
import com.example.app.chat.library.exception.NotFoundException;
import com.example.app.chat.library.utils.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
@RequiredArgsConstructor
public class MessageException {

    private final MessageService messageService;

    @MessageExceptionHandler(NotFoundException.class)
    public ResponseEntity<BaseResponse<Void>> handleNotFoundException(NotFoundException e) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(BaseResponse.<Void>builder()
                        .statusCode(HttpStatus.NOT_FOUND.value())
                        .message(messageService.getMessage(e.getMessage()))
                        .timestamp(LocalDateTime.now())
                        .build());
    }

    @ExceptionHandler(HttpRequestException.class)
    public ResponseEntity<BaseResponse<Void>> handleHttpRequestException(HttpRequestException e) {
        return ResponseEntity
                .status(e.getStatusCode())
                .body(BaseResponse.<Void>builder()
                        .statusCode(e.getStatusCode())
                        .message(messageService.getMessage(e.getMessage()))
                        .timestamp(e.getTimestamp())
                        .build());
    }
}
