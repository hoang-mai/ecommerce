package com.ecommerce.library.utils.validate;

import com.ecommerce.library.enumeration.ShopStatus;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;


public class StatusValidator implements ConstraintValidator<StatusFormat, ShopStatus> {

    private ShopStatus[] acceptedValues;

    @Override
    public void initialize(StatusFormat constraintAnnotation) {
        this.acceptedValues = constraintAnnotation.acceptedValues();
    }


    @Override
    public boolean isValid(ShopStatus value, ConstraintValidatorContext context) {
        if(value == null) {
            return true;
        }
        return Arrays.asList(acceptedValues).contains(value);
    }
}
