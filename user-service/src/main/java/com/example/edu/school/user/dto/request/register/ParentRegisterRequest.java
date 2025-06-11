package com.example.edu.school.user.dto.request.register;

import com.example.edu.school.user.model.ParentRelationship;
import com.example.edu.school.user.validation.annotation.PhoneSubSet;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class ParentRegisterRequest extends RegisterRequest{

    @NotNull(message = "Mối quan hệ không được để trống")
    private ParentRelationship relationship;

    @NotNull(message = "Số điện thoại không được để trống")
    @PhoneSubSet
    @Schema(description = "Số điện thoại của phụ huynh" ,example = "0123456789")
    @JsonProperty("phone_number")
    private String phoneNumber;
}
