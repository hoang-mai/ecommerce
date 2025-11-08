package com.ecommerce.user.dto;

import com.ecommerce.library.utils.MessageError;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class ReqRejectOwnerDTO {

    @NotBlank(message = MessageError.REASON_NOT_BLANK)
    @Schema(description = "Reason for rejecting the owner registration", example = "Incomplete documentation")
    private String reason;
}
