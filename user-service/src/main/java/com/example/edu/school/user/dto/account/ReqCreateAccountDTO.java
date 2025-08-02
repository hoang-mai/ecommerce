package com.example.edu.school.user.dto.account;

import com.example.edu.school.library.enumeration.Gender;
import com.example.edu.school.library.enumeration.Role;
import com.example.edu.school.library.utils.MessageError;
import com.example.edu.school.library.utils.validate.PhoneNumberFormat;
import com.example.edu.school.library.utils.validate.RoleFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class ReqCreateAccountDTO {
    private String email;
    private String password;
    private Role role;
}
