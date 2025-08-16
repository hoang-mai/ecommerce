package com.example.app.chat.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ParentStudentRepository extends JpaRepository<ParentStudent,Long> {

    boolean existsByParentAndStudent(Parent parent, Student student);

    Optional<ParentStudent> findByParentAndStudent(Parent parent, Student student);
}
