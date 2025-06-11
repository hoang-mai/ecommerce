package com.example.edu.school.user.repository;

import com.example.edu.school.user.model.Parent;
import com.example.edu.school.user.model.ParentStudent;
import com.example.edu.school.user.model.Student;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ParentStudentRepository extends JpaRepository<ParentStudent,Long> {

    boolean existsByParentAndStudent(Parent parent, Student student);

    Optional<ParentStudent> findByParentAndStudent(Parent parent, Student student);
}
