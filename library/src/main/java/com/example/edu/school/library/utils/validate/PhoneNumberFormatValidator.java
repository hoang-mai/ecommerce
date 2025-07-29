package com.example.edu.school.library.utils.validate;

import com.example.edu.school.library.utils.validate.PhoneNumberFormat;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PhoneNumberFormatValidator implements ConstraintValidator<PhoneNumberFormat, String> {

    /*
     * Số điện thoại có 10 chữ số, bắt đầu bằng số 0.
     */
    @Override
    public boolean isValid(String phoneNumber, ConstraintValidatorContext context) {
        if(phoneNumber == null || phoneNumber.isEmpty()) {
            return true;
        }
        String regex = "0[0-9]{9}";
        return phoneNumber.matches(regex);
    }

}
