package com.example.app.chat.library.utils.validate;

import com.example.app.chat.library.utils.MessageError;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = RoleFormatValidator.class)
public @interface AccountStatusFormat {
    String message() default MessageError.INVALID_ACCOUNT_STATUS;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
