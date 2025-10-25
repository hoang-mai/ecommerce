package com.ecommerce.library.utils.validate;

import com.ecommerce.library.enumeration.Role;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;

public class RoleFormatValidator implements ConstraintValidator<RoleFormat, Role> {


    @Override
    public boolean isValid(Role role, ConstraintValidatorContext context) {
        if(role == null) {
            return true;
        }
        return Arrays.asList(Role.values()).contains(role);
    }

}
