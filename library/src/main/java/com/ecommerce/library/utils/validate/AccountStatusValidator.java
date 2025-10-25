package com.ecommerce.library.utils.validate;

import com.ecommerce.library.enumeration.AccountStatus;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;

public class AccountStatusValidator implements ConstraintValidator<AccountStatusFormat, AccountStatus> {
    @Override
    public boolean isValid(AccountStatus value, ConstraintValidatorContext context) {
        if(value == null) {
            return true;
        }
        return Arrays.asList(AccountStatus.values()).contains(value);
    }
}
