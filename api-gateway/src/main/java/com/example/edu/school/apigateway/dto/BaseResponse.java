package com.example.edu.school.apigateway.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

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


}