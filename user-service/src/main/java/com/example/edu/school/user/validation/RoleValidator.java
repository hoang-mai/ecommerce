package com.example.edu.school.user.validation;

import com.example.edu.school.user.model.Role;
import com.example.edu.school.user.validation.annotation.RoleSubSet;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class RoleValidator implements ConstraintValidator<RoleSubSet, Role> {

    private Role[] anyOf;

    @Override
    public void initialize(RoleSubSet constraintAnnotation) {
        this.anyOf = constraintAnnotation.anyOf();
    }

    @Override
    public boolean isValid(Role value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        if (anyOf == null || anyOf.length == 0) {
            return true;
        }
        for (Role role : anyOf) {
            if (role == value) {
                return true;
            }
        }
        return false;
    }

}
