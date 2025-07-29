package com.example.edu.school.user.entity;

import java.util.Set;

import com.example.edu.school.library.enumeration.Subject;
import jakarta.persistence.*;

import lombok.*;
import lombok.experimental.SuperBuilder;


@Entity
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "teachers")
public class Teacher extends User{

    @Enumerated(EnumType.STRING)
    @ElementCollection(targetClass = Subject.class,fetch = FetchType.EAGER)
    @CollectionTable(name = "teacher_subjects",joinColumns = @JoinColumn(name = "teacher_id"))
    @Column(name = "subject")
    private Set<Subject> subjects;

    @Column(name = "teacher_code_number", unique = true, nullable = false)
    private String teacherCodeNumber;

}