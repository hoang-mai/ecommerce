package com.example.app.chat.library.exception;

/**
 * Ngoại lệ khi có dữ liệu trùng lặp.
 */
public class DuplicateException extends RuntimeException {
    public DuplicateException(String message) {
        super(message);
    }
}
