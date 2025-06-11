package com.example.edu.school.user.model;

import java.util.Set;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;

import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
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


}