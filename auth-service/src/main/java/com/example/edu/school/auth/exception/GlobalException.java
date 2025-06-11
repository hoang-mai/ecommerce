package com.example.edu.school.auth.exception;

import com.example.edu.school.auth.dto.response.BaseResponse;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalException {

    @ExceptionHandler(DuplicateUserException.class)
    public ResponseEntity<BaseResponse<ExceptionDetail>> handleDuplicateUserException(DuplicateUserException e, HttpServletRequest httpServletRequest) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(BaseResponse.<ExceptionDetail>builder()
                        .statusCode(HttpStatus.BAD_REQUEST.value())
                        .message(e.getMessage())
                        .data(ExceptionDetail.fromException(e, httpServletRequest))
                        .build());
    }
    @ExceptionHandler(EmailNotFoundException.class)
    public ResponseEntity<BaseResponse<ExceptionDetail>> handleEmailNotFoundException(EmailNotFoundException e, HttpServletRequest httpServletRequest) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(BaseResponse.<ExceptionDetail>builder()
                        .statusCode(HttpStatus.NOT_FOUND.value())
                        .message(e.getMessage())
                        .data(ExceptionDetail.fromException(e, httpServletRequest))
                        .build());
    }
    @ExceptionHandler(FeignException.class)
    public ResponseEntity<BaseResponse<ExceptionDetail>> handleFeignException(FeignException e, HttpServletRequest httpServletRequest) {
        BaseResponse<ExceptionDetail> errorResponse = e.getResponse();
        return ResponseEntity
                .status(errorResponse.getStatusCode())
                .body(errorResponse);
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BaseResponse<ExceptionDetail>> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException e, HttpServletRequest httpServletRequest) {
        String errorMessage = e.getBindingResult().getFieldError().getDefaultMessage();
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(BaseResponse.<ExceptionDetail>builder()
                        .statusCode(HttpStatus.BAD_REQUEST.value())
                        .message(errorMessage)
                        .data(ExceptionDetail.fromException(e, httpServletRequest))
                        .build());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<BaseResponse<ExceptionDetail>> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException e, HttpServletRequest httpServletRequest) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(BaseResponse.<ExceptionDetail>builder()
                        .statusCode(HttpStatus.BAD_REQUEST.value())
                        .message("Dữ liệu không hợp lệ")
                        .data(ExceptionDetail.fromException(e, httpServletRequest))
                        .build());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<BaseResponse<ExceptionDetail>> handleIllegalArgumentException(IllegalArgumentException e,
                                                                                        HttpServletRequest httpServletRequest) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(BaseResponse.<ExceptionDetail>builder()
                        .statusCode(HttpStatus.BAD_REQUEST.value())
                        .message(e.getMessage())
                        .data(ExceptionDetail.fromException(e, httpServletRequest))
                        .build());
    }
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<BaseResponse<ExceptionDetail>> handleBadCredentialsException(BadCredentialsException e,
                                                                                          HttpServletRequest httpServletRequest) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(BaseResponse.<ExceptionDetail>builder()
                        .statusCode(HttpStatus.UNAUTHORIZED.value())
                        .message("Sai tên đăng nhập hoặc mật khẩu")
                        .data(ExceptionDetail.fromException(e, httpServletRequest))
                        .build());
    }
}
