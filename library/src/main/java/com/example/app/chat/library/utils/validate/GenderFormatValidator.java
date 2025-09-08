package com.example.app.chat.library.utils.validate;

import com.example.app.chat.library.enumeration.Gender;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;

public class GenderFormatValidator implements ConstraintValidator<GenderFormat, Gender> {
    @Override
    public boolean isValid(Gender value, ConstraintValidatorContext context) {
        if(value == null) {
            return true;
        }
        return Arrays.asList(Gender.values()).contains(value);
    }
}
