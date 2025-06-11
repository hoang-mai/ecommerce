package com.example.edu.school.auth.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class ValidateAccessTokenRequest {

    @JsonProperty("access_token")
    @NotBlank
    private String accessToken;
}
