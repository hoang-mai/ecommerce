package com.example.edu.school.auth.exception;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
public class ExceptionDetail {


    private LocalDateTime timestamp;
    private String path;
    private String cause;

    @JsonProperty("exception_name")
    private String exceptionName;

    public static ExceptionDetail fromException(Exception exception, HttpServletRequest httpServletRequest) {
        String cause = exception.getCause() != null ? exception.getCause().toString() : null;
        return ExceptionDetail.builder()
                .timestamp(LocalDateTime.now())
                .cause(cause)
                .path(httpServletRequest.getServletPath())
                .exceptionName(exception.getClass().getName())
                .build();
    }

    @JsonCreator
    public ExceptionDetail(
            @JsonProperty("timestamp") LocalDateTime timestamp,
            @JsonProperty("path") String path,
            @JsonProperty("cause") String cause,
            @JsonProperty("exception_name") String exceptionName
            ) {
        this.timestamp = timestamp;
        this.path = path;
        this.cause = cause;
        this.exceptionName = exceptionName;
    }
}
