package com.example.app.chat.library.utils.validate;

import com.example.app.chat.library.enumeration.Role;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;

public class RoleFormatValidator implements ConstraintValidator<RoleFormat, Role> {


    @Override
    public boolean isValid(Role role, ConstraintValidatorContext context) {
        return role != null && Arrays.asList(Role.values()).contains(role);
    }

}
