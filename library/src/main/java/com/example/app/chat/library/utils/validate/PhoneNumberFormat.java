package com.example.app.chat.library.utils.validate;

import com.example.app.chat.library.utils.MessageError;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = PhoneNumberFormatValidator.class)
public @interface PhoneNumberFormat {

    String message() default MessageError.INVALID_PHONE_NUMBER;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
