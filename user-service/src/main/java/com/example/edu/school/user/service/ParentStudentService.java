package com.example.edu.school.user.service;

import com.example.edu.school.user.model.Parent;
import com.example.edu.school.user.model.ParentRelationship;
import com.example.edu.school.user.model.Student;

public interface ParentStudentService {
    void addParentToStudent(Parent parent, Student student, ParentRelationship relationship);

    void updateParentToStudent(Parent parent, Student student, ParentRelationship relationship);
}
