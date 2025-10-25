package com.ecommerce.library.utils.validate;

import com.ecommerce.library.enumeration.Gender;
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
