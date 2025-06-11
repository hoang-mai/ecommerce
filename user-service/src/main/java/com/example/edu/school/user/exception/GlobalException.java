package com.example.edu.school.user.exception;

import com.example.edu.school.user.dto.response.BaseResponse;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalException {

        @ExceptionHandler(DuplicateUserException.class)
        public ResponseEntity<BaseResponse<ExceptionDetail>> handleDuplicateUserException(DuplicateUserException e,
                        HttpServletRequest httpServletRequest) {
                return ResponseEntity
                                .status(HttpStatus.BAD_REQUEST)
                                .body(BaseResponse.<ExceptionDetail>builder()
                                                .statusCode(HttpStatus.BAD_REQUEST.value())
                                                .message(e.getMessage())
                                                .data(ExceptionDetail.fromException(e, httpServletRequest))
                                                .build());
        }

        @ExceptionHandler(EmailNotFoundException.class)
        public ResponseEntity<BaseResponse<ExceptionDetail>> handleEmailNotFoundException(EmailNotFoundException e,
                        HttpServletRequest httpServletRequest) {
                return ResponseEntity
                                .status(HttpStatus.NOT_FOUND)
                                .body(BaseResponse.<ExceptionDetail>builder()
                                                .statusCode(HttpStatus.NOT_FOUND.value())
                                                .message(e.getMessage())
                                                .data(ExceptionDetail.fromException(e, httpServletRequest))
                                                .build());
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

        @ExceptionHandler(UserIdNotFoundException.class)
        public ResponseEntity<BaseResponse<ExceptionDetail>> handleUserIdNotFoundException(UserIdNotFoundException e,
                        HttpServletRequest httpServletRequest) {
                return ResponseEntity
                                .status(HttpStatus.NOT_FOUND)
                                .body(BaseResponse.<ExceptionDetail>builder()
                                                .statusCode(HttpStatus.NOT_FOUND.value())
                                                .message(e.getMessage())
                                                .data(ExceptionDetail.fromException(e, httpServletRequest))
                                                .build());
        }
}