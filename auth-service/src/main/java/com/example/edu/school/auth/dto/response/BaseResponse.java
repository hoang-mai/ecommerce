package com.example.edu.school.auth.dto.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;


@Builder
@Getter
public class BaseResponse<T> {

    @JsonProperty("status_code")
    private int statusCode;

    @JsonProperty("message")
    private String message;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("data")
    private T data;

    @JsonCreator
    public BaseResponse(
            @JsonProperty("status_code") int statusCode,
            @JsonProperty("message") String message,
            @JsonProperty("data") T data) {
        this.statusCode = statusCode;
        this.message = message;
        this.data = data;
    }
}
