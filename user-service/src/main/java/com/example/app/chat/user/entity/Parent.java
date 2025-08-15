package com.example.app.chat.user.entity;

import java.util.Set;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "parents")
public class Parent extends User {

    @OneToMany(mappedBy = "parent")
    private Set<ParentStudent> parentStudents;

    @Column(name = "parent_code_number", unique = true, nullable = false)
    private String parentCodeNumber;

}