package com.ecommerce.chat.exception;

import com.ecommerce.library.component.MessageService;
import com.ecommerce.library.exception.HttpRequestException;
import com.ecommerce.library.exception.NotFoundException;
import com.ecommerce.library.utils.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
@RequiredArgsConstructor
public class MessageException {

    private final MessageService messageService;

    @SendToUser("/queue/errors")
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

    @SendToUser("/queue/errors")
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
