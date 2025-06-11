package com.example.edu.school.user.validation.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.example.edu.school.user.validation.PhoneValidator;


@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Constraint(validatedBy = PhoneValidator.class)
public @interface PhoneSubSet {
    String message() default "Số điện thoại gồm 10 chữ số";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
