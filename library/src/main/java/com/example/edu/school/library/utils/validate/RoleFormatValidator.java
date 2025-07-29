package com.example.edu.school.library.utils.validate;

import com.example.edu.school.library.enumeration.Role;
import com.example.edu.school.library.utils.validate.RoleFormat;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;

public class RoleFormatValidator implements ConstraintValidator<RoleFormat, Role> {


    @Override
    public boolean isValid(Role role, ConstraintValidatorContext context) {
        return role != null && Arrays.asList(Role.values()).contains(role);
    }

}
