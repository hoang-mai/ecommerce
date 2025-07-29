package com.example.edu.school.user.service.impl;

import com.example.edu.school.library.exception.NotFoundException;
import com.example.edu.school.user.dto.update.ParentRelationshipUpdateRequest;
import com.example.edu.school.user.dto.update.TeacherUpdateRequest;
import com.example.edu.school.user.dto.update.UserUpdateRequest;
import com.example.edu.school.user.dto.user.ReqCreateUserDTO;
import com.example.edu.school.user.service.ParentStudentService;
import com.example.edu.school.user.service.UserService;
import com.example.edu.school.library.utils.PageResponse;
import com.example.edu.school.user.dto.user.UserPreviewResponse;
import com.example.edu.school.user.dto.information.UserResponse;
import com.example.edu.school.library.enumeration.Role;
import com.example.edu.school.user.entity.*;
import com.example.edu.school.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@RequiredArgsConstructor
@Service
public class UserServiceImp implements UserService {
    private final UserRepository userRepository;
    private final ParentStudentService parentStudentService;

    @Transactional
    @Override
    public void create(ReqCreateUserDTO reqCreateUserDTO) {
        User user = User.builder()
                .accountId(reqCreateUserDTO.getAccountId())
                .firstName(reqCreateUserDTO.getFirstName())
                .middleName(reqCreateUserDTO.getMiddleName())
                .lastName(reqCreateUserDTO.getLastName())
                .role(reqCreateUserDTO.getRole())
                .gender(reqCreateUserDTO.getGender())
                .phoneNumber(reqCreateUserDTO.getPhoneNumber())
                .dateOfBirth(reqCreateUserDTO.getDateOfBirth())
                .address(reqCreateUserDTO.getAddress())
                .email(reqCreateUserDTO.getEmail())
                .build();
        userRepository.save(user);
//        switch (reqCreateUserDTO.getRole()) {
//            case ADMIN, ASSISTANT, PRINCIPAL:
//
//                userRepository.save(user);
//                break;
//            case TEACHER:
//                Teacher teacher = new Teacher();
//                userRepository.save(teacher);
//                break;
//            case STUDENT:
//                Student student = Student.builder()
//                        .accountId(reqCreateUserDTO.getAccountId())
//                        .email(reqCreateUserDTO.getEmail())
//                        .firstName(reqCreateUserDTO.getFirstName())
//                        .middleName(reqCreateUserDTO.getMiddleName())
//                        .lastName(reqCreateUserDTO.getLastName())
//                        .role(reqCreateUserDTO.getRole())
//                        .gender(reqCreateUserDTO.getGender())
//                        .dateOfBirth(reqCreateUserDTO.getDateOfBirth())
//                        .email(reqCreateUserDTO.getEmail())
//                        .build();
//                userRepository.save(student);
//                break;
//            default:
//                throw new IllegalArgumentException("Vai trò không hợp lệ");
//        }
    }


    @Override
    public PageResponse<List<UserPreviewResponse>> getUsers(int page, int size, Role role) {
        return null;
    }


    @Override
    public UserResponse getUserById(Long id) {
        return null;
    }

    @Override
    public void updateUser(Long userId, UserUpdateRequest userUpdateRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Id người dùng không tồn tại"));
        user.setAddress(userUpdateRequest.getAddress());
        user.setAvatarUrl(userUpdateRequest.getAvatarUrl());
        user.setDateOfBirth(userUpdateRequest.getDateOfBirth());
        user.setPhoneNumber(userUpdateRequest.getPhoneNumber());
        user.setGender(userUpdateRequest.getGender());
        if (user.getRole() == Role.TEACHER && ((TeacherUpdateRequest) userUpdateRequest).getSubjects() != null && !((TeacherUpdateRequest) userUpdateRequest).getSubjects().isEmpty()) {
            ((Teacher) user).setSubjects(((TeacherUpdateRequest) userUpdateRequest).getSubjects());
        }
        userRepository.save(user);
    }


}
 