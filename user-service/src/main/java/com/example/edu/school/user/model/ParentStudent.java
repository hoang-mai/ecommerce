package com.example.edu.school.user.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "parent_students")
public class ParentStudent {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "parent_student_id")
    private Long parentStudentId;

    @Enumerated(EnumType.STRING)
    @Column(name = "parent_relationship")
    private ParentRelationship parentRelationship;

    @ManyToOne
    private Parent parent;

    @ManyToOne
    private Student student;

}
