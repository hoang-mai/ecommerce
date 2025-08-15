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
@Table(name = "students")
public class Student extends User {


    @OneToMany(mappedBy = "student")
    private Set<ParentStudent> parentStudents;

    @Column(name = "student_code_number", unique = true, nullable = false)
    private String studentCodeNumber;

}
