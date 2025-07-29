package com.example.edu.school.user.dto.information;

import com.example.edu.school.library.enumeration.Gender;
import com.example.edu.school.library.enumeration.ParentRelationship;

import com.example.edu.school.library.enumeration.Role;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Setter
@Getter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class ParentResponse extends UserResponse {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private ParentRelationship parentRelationship;

    private Set<StudentResponse> students;

    public ParentResponse(
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
            LocalDateTime updatedAt,

            ParentRelationship parentRelationship) {
        super(
                userId,
                firstName,
                middleName,
                lastName,
                phoneNumber,
                email,
                role,
                gender,
                dateOfBirth,
                codeNumber,
                address,
                avatarUrl,
                createdAt,
                updatedAt);
        this.parentRelationship = parentRelationship;
    }
}
