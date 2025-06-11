package com.example.edu.school.auth.validation;

import com.example.edu.school.auth.validation.annotation.PhoneSubSet;

public class PhoneValidator implements jakarta.validation.ConstraintValidator<PhoneSubSet, String> {

    @Override
    public boolean isValid(String value, jakarta.validation.ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        return value.matches("\\d{10}");
    }

}
