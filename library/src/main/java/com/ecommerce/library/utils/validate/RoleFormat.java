package com.ecommerce.library.utils.validate;

import com.ecommerce.library.utils.MessageError;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = RoleFormatValidator.class)
public @interface RoleFormat {
    String message() default MessageError.INVALID_ROLE;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
