package com.example.edu.school.library.exception;

/**
 * Ngoại lệ khi có dữ liệu trùng lặp.
 */
public class DuplicateException extends RuntimeException {
    public DuplicateException(String message) {
        super(message);
    }
}
