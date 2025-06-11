package com.example.edu.school.user.service;

import com.example.edu.school.user.model.Parent;
import com.example.edu.school.user.model.ParentRelationship;
import com.example.edu.school.user.model.ParentStudent;
import com.example.edu.school.user.model.Student;
import com.example.edu.school.user.repository.ParentStudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ParentStudentServiceImp implements ParentStudentService{
    private final ParentStudentRepository parentStudentRepository;

    @Override
    public void addParentToStudent(Parent parent, Student student, ParentRelationship relationship) {
        if(parentStudentRepository.existsByParentAndStudent(parent, student)) {
            throw new IllegalArgumentException("Phụ huynh đã có học sinh này trong danh sách");
   
        }
        ParentStudent parentStudent = ParentStudent.builder()
                .parent(parent)
                .student(student)
                .parentRelationship(relationship)
                .build();
        parentStudentRepository.save(parentStudent);
    }

    @Override
    public void updateParentToStudent(Parent parent, Student student, ParentRelationship relationship) {
        ParentStudent parentStudent = parentStudentRepository.findByParentAndStudent(parent, student)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy phụ huynh và học sinh này"));
        parentStudent.setParentRelationship(relationship);
        parentStudentRepository.save(parentStudent);
    }
}
