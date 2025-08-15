package com.example.app.chat.user.dto.user;

import com.example.app.chat.library.enumeration.ParentRelationship;
import com.example.app.chat.library.utils.MessageError;
import com.example.app.chat.library.utils.validate.PhoneNumberFormat;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class ParentReqCreateUserDTO extends ReqCreateUserDTO {

    @NotNull(message = "Mối quan hệ không được để trống")
    private ParentRelationship relationship;

    @NotNull(message = MessageError.PHONE_NUMBER_NOT_BLANK)
    @PhoneNumberFormat
    @Schema(description = "Số điện thoại của phụ huynh" ,example = "0123456789")
    private String phoneNumber;
}
