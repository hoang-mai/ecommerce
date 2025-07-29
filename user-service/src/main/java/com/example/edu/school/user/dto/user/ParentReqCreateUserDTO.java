package com.example.edu.school.user.dto.user;

import com.example.edu.school.library.enumeration.ParentRelationship;
import com.example.edu.school.library.utils.MessageError;
import com.example.edu.school.library.utils.validate.PhoneNumberFormat;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class ParentReqCreateUserDTO extends ReqCreateUserDTO {

    @NotNull(message = "Mối quan hệ không được để trống")
    private ParentRelationship relationship;

    @NotNull(message = MessageError.PHONE_NUMBER_NOT_BLANK)
    @PhoneNumberFormat
    @Schema(description = "Số điện thoại của phụ huynh" ,example = "0123456789")
    private String phoneNumber;
}
