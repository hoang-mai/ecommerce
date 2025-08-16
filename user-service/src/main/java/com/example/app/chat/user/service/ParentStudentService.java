package com.example.app.chat.user.service;

public interface ParentStudentService {
    void addParentToStudent(Parent parent, Student student, ParentRelationship relationship);

    void updateParentToStudent(Parent parent, Student student, ParentRelationship relationship);
}
