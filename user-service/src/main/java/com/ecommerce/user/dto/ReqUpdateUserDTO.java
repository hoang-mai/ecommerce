package com.ecommerce.user.dto;

import java.time.LocalDate;

import com.ecommerce.library.enumeration.Gender;
import com.ecommerce.library.utils.MessageError;
import com.ecommerce.library.utils.validate.GenderFormat;
import com.ecommerce.library.utils.validate.PhoneNumberFormat;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class ReqUpdateUserDTO {

    @Email
    private String email;

    private String description;
    private String firstName;
    private String middleName;

    @Size(min = 1, max = 10, message = MessageError.NAME_SIZE)
    private String lastName;

    @PhoneNumberFormat
    private String phoneNumber;

    @Past(message = MessageError.INVALID_DATE_OF_BIRTH)
    private LocalDate dateOfBirth;

    @GenderFormat
    private Gender gender;
}
