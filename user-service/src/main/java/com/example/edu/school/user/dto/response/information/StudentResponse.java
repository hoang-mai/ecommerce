package com.example.edu.school.user.dto.response.information;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

import com.example.edu.school.user.model.Gender;
import com.example.edu.school.user.model.ParentRelationship;
import com.example.edu.school.user.model.Role;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.experimental.SuperBuilder;


@SuperBuilder
@Getter
public class StudentResponse extends UserResponse{
    private Set<ParentResponse> parents;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private ParentRelationship parentRelationship;

    public StudentResponse(
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
        ParentRelationship parentRelationship
    ) {
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
                updatedAt
        );
        this.parentRelationship = parentRelationship;
    }
}
