package com.example.edu.school.auth.validation.annotation;

import com.example.edu.school.auth.model.Role;
import com.example.edu.school.auth.validation.RoleValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;
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
