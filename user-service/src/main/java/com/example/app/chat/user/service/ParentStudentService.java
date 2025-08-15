package com.example.app.chat.user.service;

import com.example.app.chat.user.entity.Parent;
import com.example.app.chat.library.enumeration.ParentRelationship;
import com.example.app.chat.user.entity.Student;

public interface ParentStudentService {
    void addParentToStudent(Parent parent, Student student, ParentRelationship relationship);

    void updateParentToStudent(Parent parent, Student student, ParentRelationship relationship);
}
