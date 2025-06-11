package com.example.edu.school.user.model;

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

}