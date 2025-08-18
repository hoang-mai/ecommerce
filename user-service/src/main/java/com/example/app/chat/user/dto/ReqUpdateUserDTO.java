package com.example.app.chat.user.dto;

import java.time.LocalDate;

import com.example.app.chat.library.enumeration.Gender;
import com.example.app.chat.library.utils.validate.PhoneNumberFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class ReqUpdateUserDTO {

    private String email;
    private String firstName;
    private String middleName;
    private String lastName;
    private String address;
    private String phoneNumber;
    private LocalDate dateOfBirth;
    private Gender gender;
}
