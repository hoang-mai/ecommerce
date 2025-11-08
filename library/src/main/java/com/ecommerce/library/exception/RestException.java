package com.ecommerce.library.exception;

import com.ecommerce.library.component.MessageService;
import com.ecommerce.library.utils.BaseResponse;
import com.ecommerce.library.utils.MessageError;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;


@ControllerAdvice
@RequiredArgsConstructor
public class RestException {

    private final MessageService messageService;

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

    @ExceptionHandler(DuplicateException.class)
    public ResponseEntity<BaseResponse<Void>> handleDuplicateException(DuplicateException e) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(BaseResponse.<Void>builder()
                        .statusCode(HttpStatus.CONFLICT.value())
                        .message(messageService.getMessage(e.getMessage()))
                        .timestamp(LocalDateTime.now())
                        .build());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BaseResponse<Void>> handleValidationException(MethodArgumentNotValidException e) {
        return ResponseEntity
                .badRequest()
                .body(BaseResponse.<Void>builder()
                        .statusCode(HttpStatus.BAD_REQUEST.value())
                        .message(messageService.getMessage(e.getBindingResult().getFieldError() != null ? e.getBindingResult().getFieldError().getDefaultMessage() : MessageError.INVALID_INPUT_DATA))
                        .timestamp(LocalDateTime.now())
                        .build());
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<BaseResponse<Void>> handleNotFoundException(NotFoundException e) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(BaseResponse.<Void>builder()
                        .statusCode(HttpStatus.NOT_FOUND.value())
                        .message(messageService.getMessage(e.getMessage()))
                        .timestamp(LocalDateTime.now())
                        .build());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<BaseResponse<Void>> handleIllegalArgumentException(IllegalArgumentException e) {
        return ResponseEntity
                .badRequest()
                .body(BaseResponse.<Void>builder()
                        .statusCode(HttpStatus.BAD_REQUEST.value())
                        .message(messageService.getMessage(e.getMessage()))
                        .timestamp(LocalDateTime.now())
                        .build());
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<BaseResponse<Void>> handleIllegalStateException(IllegalStateException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(BaseResponse.<Void>builder()
                        .statusCode(HttpStatus.BAD_REQUEST.value())
                        .message(messageService.getMessage(e.getMessage()))
                        .timestamp(LocalDateTime.now())
                        .build());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseResponse<Void>> handleGenericException(Exception e) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(BaseResponse.<Void>builder()
                        .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                        .message(messageService.getMessage(e.getMessage()))
                        .timestamp(LocalDateTime.now())
                        .build());
    }

}
