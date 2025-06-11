package com.example.edu.school.user.validation.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import jakarta.validation.Payload;
import com.example.edu.school.user.validation.RoleValidator;

import jakarta.validation.Constraint;
import com.example.edu.school.user.model.Role;
import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;
@Target({ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = RoleValidator.class)
@Documented
public @interface RoleSubSet {
    Role[] anyOf() default {};

    String message() default "Vai trò không hợp lệ";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
