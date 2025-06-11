package com.example.edu.school.auth.validation.annotation;

import com.example.edu.school.auth.validation.PhoneValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;


@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Constraint(validatedBy = PhoneValidator.class)
public @interface PhoneSubSet {
    String message() default "Số điện thoại gồm 10 chữ số";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
