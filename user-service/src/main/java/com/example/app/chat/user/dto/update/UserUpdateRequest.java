package com.example.app.chat.user.dto.update;

import java.time.LocalDate;

import com.example.app.chat.library.enumeration.Gender;
import com.example.app.chat.library.utils.validate.PhoneNumberFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "role",defaultImpl = UserUpdateRequest.class)
@JsonSubTypes({
        @JsonSubTypes.Type(value = TeacherUpdateRequest.class, name = "TEACHER"),
})
@Getter
public class UserUpdateRequest {

    @Schema(description = "Địa chỉ", example = "Hà Nội")
    private String address;

    @PhoneNumberFormat
    @JsonProperty("phone_number")
    @Schema(description = "Số điện thoại", example = "0123456789")
    private String phoneNumber;

    @Schema(description = "Giới tính", example = "FEMALE")
    private Gender gender;


    @JsonProperty("avatar_url")
    private String avatarUrl;

    @Schema(description = "Năm sinh", example = "2000-02-15")
    @JsonProperty("date_of_birth")
    private LocalDate dateOfBirth;
    
}
