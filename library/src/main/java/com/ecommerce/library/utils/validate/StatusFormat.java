package com.ecommerce.library.utils.validate;

import com.ecommerce.library.enumeration.ShopStatus;
import com.ecommerce.library.utils.MessageError;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = StatusValidator.class)
public @interface StatusFormat {
    String message() default MessageError.INVALID_STATUS;

    ShopStatus[] acceptedValues() default {ShopStatus.ACTIVE, ShopStatus.INACTIVE, ShopStatus.SUSPENDED};

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
