package com.example.app.chat.library.utils.validate;

import com.example.app.chat.library.utils.MessageError;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = GenderFormatValidator.class)
public @interface GenderFormat {
    String message() default MessageError.INVALID_GENDER;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
