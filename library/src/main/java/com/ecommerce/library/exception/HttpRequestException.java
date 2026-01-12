package com.ecommerce.library.exception;

import lombok.Getter;

import java.time.Instant;

/**
 * Ngoại lệ khi gửi yêu cầu HTTP không thành công.
 */
@Getter
public class HttpRequestException extends RuntimeException {
    private final int statusCode;
    private final Instant timestamp;
    public HttpRequestException(String message, int statusCode, Instant timestamp) {
        super(message);
        this.statusCode = statusCode;
        this.timestamp = timestamp;
    }
}
