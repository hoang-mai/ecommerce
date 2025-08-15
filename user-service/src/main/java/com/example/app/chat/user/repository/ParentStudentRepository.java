package com.example.app.chat.user.repository;

import com.example.app.chat.user.entity.Parent;
import com.example.app.chat.user.entity.ParentStudent;
import com.example.app.chat.user.entity.Student;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ParentStudentRepository extends JpaRepository<ParentStudent,Long> {

    boolean existsByParentAndStudent(Parent parent, Student student);

    Optional<ParentStudent> findByParentAndStudent(Parent parent, Student student);
}
