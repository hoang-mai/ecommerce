package com.example.edu.school.library.exception;

import com.example.edu.school.library.component.MessageService;
import com.example.edu.school.library.utils.BaseResponse;
import com.example.edu.school.library.utils.MessageError;
import io.temporal.failure.ActivityFailure;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;


@ControllerAdvice
@RequiredArgsConstructor
public class GlobalException {

    private final MessageService messageService;

    @ExceptionHandler(HttpRequestException.class)
    public ResponseEntity<BaseResponse<Void>> handleBadRequestException(HttpRequestException e) {
        return ResponseEntity
                .status(e.getStatusCode())
                .body(BaseResponse.<Void>builder()
                        .statusCode(e.getStatusCode())
                        .message(messageService.getMessage(e.getMessage()))
                        .timestamp(e.getTimestamp())
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

}
