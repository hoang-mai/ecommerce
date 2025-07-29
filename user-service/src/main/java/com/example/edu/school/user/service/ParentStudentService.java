package com.example.edu.school.user.service;

import com.example.edu.school.user.entity.Parent;
import com.example.edu.school.library.enumeration.ParentRelationship;
import com.example.edu.school.user.entity.Student;

public interface ParentStudentService {
    void addParentToStudent(Parent parent, Student student, ParentRelationship relationship);

    void updateParentToStudent(Parent parent, Student student, ParentRelationship relationship);
}
