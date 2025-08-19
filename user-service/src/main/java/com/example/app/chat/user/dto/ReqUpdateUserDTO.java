package com.example.app.chat.user.dto;

import java.time.LocalDate;

import com.example.app.chat.library.enumeration.Gender;
import com.example.app.chat.library.utils.MessageError;
import com.example.app.chat.library.utils.validate.PhoneNumberFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class ReqUpdateUserDTO {

    private String email;
    private String description;
    private String firstName;
    private String middleName;

    @Size(min = 1, max = 10, message = MessageError.NAME_SIZE)
    private String lastName;
    private String address;
    private String phoneNumber;
    private LocalDate dateOfBirth;
    private Gender gender;
}
