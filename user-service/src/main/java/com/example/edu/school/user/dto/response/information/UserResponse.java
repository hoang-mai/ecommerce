package com.example.edu.school.user.dto.response.information;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.example.edu.school.user.model.Gender;
import com.example.edu.school.user.model.Role;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Setter
@Getter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {

    @JsonProperty("user_id")
    private Long userId;
    private String email;

    @JsonProperty("code_number")
    private String codeNumber;

    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty("middle_name")
    private String middleName;

    @JsonProperty("last_name")
    private String lastName;

    @JsonProperty("address")
    private String address;

    @JsonProperty("phone_number")
    private String phoneNumber;

    @JsonProperty("avatar_url")
    private String avatarUrl;

    @JsonProperty("date_of_birth")
    private LocalDate dateOfBirth;


    private Role role;

    private Gender gender;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;

    public UserResponse(
        Long userId,
        String firstName,
        String middleName,
        String lastName,
        String phoneNumber,
        String email,
        Role role,
        Gender gender,
        LocalDate dateOfBirth,
        String codeNumber,
        String address,
        String avatarUrl,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
    ) {
        this.userId = userId;
        this.email = email;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.codeNumber = codeNumber;
        this.address = address;
        this.avatarUrl = avatarUrl;
        this.dateOfBirth = dateOfBirth;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.role = role;
        this.gender = gender;
    }
}
