package com.example.edu.school.user.service;

import com.example.edu.school.user.dto.request.update.ParentRelationshipUpdateRequest;
import com.example.edu.school.user.dto.request.update.TeacherUpdateRequest;
import com.example.edu.school.user.dto.request.update.UserUpdateRequest;
import com.example.edu.school.user.dto.request.register.ParentRegisterRequest;
import com.example.edu.school.user.dto.request.register.RegisterRequest;
import com.example.edu.school.user.dto.request.register.StudentRegisterRequest;
import com.example.edu.school.user.dto.request.register.TeacherRegisterRequest;
import com.example.edu.school.user.dto.response.PageResponse;
import com.example.edu.school.user.dto.response.UserCredentialResponse;
import com.example.edu.school.user.dto.response.UserPreviewResponse;
import com.example.edu.school.user.dto.response.information.ParentResponse;
import com.example.edu.school.user.dto.response.information.StudentResponse;
import com.example.edu.school.user.dto.response.information.TeacherResponse;
import com.example.edu.school.user.dto.response.information.UserResponse;
import com.example.edu.school.user.exception.EmailNotFoundException;
import com.example.edu.school.user.exception.UserIdNotFoundException;
import com.example.edu.school.user.model.*;
import com.example.edu.school.user.repository.UserRepository;
import com.example.edu.school.user.utils.VietnameseUtils;
import lombok.RequiredArgsConstructor;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Year;
import java.util.List;

@RequiredArgsConstructor
@Service
public class UserServiceImp implements UserService {
    private final UserRepository userRepository;
    private final ParentStudentService parentStudentService;
    private final CodeNumberService codeNumberService;
    private static final String EMAIL_LAST_DOMAIN = "@school.edu";

    
    
    @Transactional
    @Override
    public String register(RegisterRequest registerRequest) {
        String codeNumber = genCodeNumber(registerRequest.getRole());
        switch (registerRequest.getRole()) {
            case ADMIN, ASSISTANT:
                User user = User.builder()
                    .firstName(registerRequest.getFirstName())
                    .middleName(registerRequest.getMiddleName())
                    .lastName(registerRequest.getLastName())
                    .password(registerRequest.getPassword())
                    .role(registerRequest.getRole())
                    .gender(registerRequest.getGender())
                    .dateOfBirth(registerRequest.getDateOfBirth())
                    .codeNumber(codeNumber)
                    .email(genEmail(registerRequest.getFirstName(), registerRequest.getMiddleName(),
                            registerRequest.getLastName(), codeNumber, registerRequest.getRole().getCode()))
                    
                    .build();
                userRepository.save(user);
                return user.getEmail();
            case TEACHER:
                Teacher teacher = Teacher.builder()
                    .firstName(registerRequest.getFirstName())
                    .middleName(registerRequest.getMiddleName())
                    .lastName(registerRequest.getLastName())
                    .password(registerRequest.getPassword())
                    .role(registerRequest.getRole())
                    .gender(registerRequest.getGender())
                    .dateOfBirth(registerRequest.getDateOfBirth())
                    .email(genEmail(registerRequest.getFirstName(), registerRequest.getMiddleName(),
                            registerRequest.getLastName(), codeNumber, registerRequest.getRole().getCode()))
                    .codeNumber(codeNumber)
                    .subjects(((TeacherRegisterRequest) registerRequest).getSubjects())
                    .build();
                userRepository.save(teacher);
                return teacher.getEmail();
            case STUDENT:
                Student student = Student.builder()
                    .firstName(registerRequest.getFirstName())
                    .middleName(registerRequest.getMiddleName())
                    .lastName(registerRequest.getLastName())
                    .password(registerRequest.getPassword())
                    .role(registerRequest.getRole())
                    .gender(registerRequest.getGender())
                    .dateOfBirth(registerRequest.getDateOfBirth())
                    .email(genEmail(registerRequest.getFirstName(), registerRequest.getMiddleName(),
                            registerRequest.getLastName(), codeNumber, registerRequest.getRole().getCode()))
                    .codeNumber(codeNumber)
                    .build();
                userRepository.save(student);
                if (((StudentRegisterRequest) registerRequest).getParents() != null && !((StudentRegisterRequest) registerRequest).getParents().isEmpty()) {
                    for (ParentRegisterRequest parentRegisterRequest : ((StudentRegisterRequest) registerRequest).getParents()) {
                        registerParent(parentRegisterRequest, student);
                    }
                }
                return student.getEmail();
            default:
                throw new IllegalArgumentException("Vai trò không hợp lệ");
        }
    }

    @Override
    public void updateParentRelationship(ParentRelationshipUpdateRequest parentRelationshipUpdateRequest) {
        User parent = userRepository.findById(parentRelationshipUpdateRequest.getParentId())
                .orElseThrow(() -> new UserIdNotFoundException("Id phụ huynh không tồn tại"));
        if (parent.getRole() != Role.PARENT) {
            throw new IllegalArgumentException("Người dùng không phải là phụ huynh");
        }
        User student = userRepository.findById(parentRelationshipUpdateRequest.getStudentId())
                .orElseThrow(() -> new UserIdNotFoundException("Id học sinh không tồn tại"));
        if (student.getRole() != Role.STUDENT) {
            throw new IllegalArgumentException("Người dùng không phải là học sinh");
        }
        parentStudentService.updateParentToStudent((Parent) parent, (Student) student,
                parentRelationshipUpdateRequest.getParentRelationship());
    }

    @Override
    public PageResponse<List<UserPreviewResponse>> searchUsers(int page, int size, String query) {
        PageRequest pageable = PageRequest.of(page, size);
        Page<UserPreviewResponse> userPage = userRepository.searchUsers(query, pageable);
        return PageResponse.<List<UserPreviewResponse>>builder()
                .pageNo(userPage.getNumber())
                .pageSize(userPage.getSize())
                .totalElements(userPage.getTotalElements())
                .totalPages(userPage.getTotalPages())
                .data(userPage.getContent())
                .build();
    }

    @Override
    public PageResponse<List<UserPreviewResponse>> getUsers(int page, int size, Role role) {
        PageRequest pageable = PageRequest.of(page, size);
        Page<UserPreviewResponse> userPage = userRepository.getUsers(role, pageable);
        return PageResponse.<List<UserPreviewResponse>>builder()
                .pageNo(userPage.getNumber())
                .pageSize(userPage.getSize())
                .totalElements(userPage.getTotalElements())
                .totalPages(userPage.getTotalPages())
                .data(userPage.getContent())
                .build();
    }

    @Transactional
    @Override
    public String registerParent(ParentRegisterRequest registerRequest, Long studentId) {
        if (registerRequest.getRole() != Role.PARENT) {
            throw new IllegalArgumentException("Vai trò không hợp lệ");
        }
        User user = userRepository.findById(studentId)
                .orElseThrow(() -> new UserIdNotFoundException("Id người dùng không tồn tại"));
        if (user.getRole() != Role.STUDENT) {
            throw new IllegalArgumentException("Người dùng không phải là học sinh");
        }
        Parent parent = userRepository
                .findParentByFirstNameAndMiddleNameAndLastNameAndPhoneNumber(registerRequest.getFirstName(),
                        registerRequest.getMiddleName(), registerRequest.getLastName(),
                        registerRequest.getPhoneNumber())
                .orElseGet(() -> {
                    Parent p = Parent.builder()
                            .firstName(registerRequest.getFirstName())
                            .middleName(registerRequest.getMiddleName())
                            .lastName(registerRequest.getLastName())
                            .password(registerRequest.getPassword())
                            .role(registerRequest.getRole())
                            .gender(registerRequest.getGender())
                            .codeNumber(registerRequest.getPhoneNumber())
                            .phoneNumber(registerRequest.getPhoneNumber())
                            .dateOfBirth(registerRequest.getDateOfBirth())
                            .email(genEmail(registerRequest.getFirstName(), registerRequest.getMiddleName(),
                                    registerRequest.getLastName(), registerRequest.getPhoneNumber(), registerRequest.getRole().getCode()))
                            .build();
                    return userRepository.save(p);
                });

        parentStudentService.addParentToStudent(parent, (Student) user, registerRequest.getRelationship());
        return parent.getEmail();
    }

    @Override
    public UserCredentialResponse getUserCredentialByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EmailNotFoundException("Email không tồn tại"));
        return UserCredentialResponse.builder()
                .email(user.getEmail())
                .password(user.getPassword())
                .role(user.getRole())
                .build();
    }

    @Override
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserIdNotFoundException("Id người dùng không tồn tại"));
        return switch (user.getRole()) {
            case ADMIN, ASSISTANT -> UserResponse.builder()
                    .userId(user.getUserId())
                    .email(user.getEmail())
                    .firstName(user.getFirstName())
                    .middleName(user.getMiddleName())
                    .lastName(user.getLastName())
                    .avatarUrl(user.getAvatarUrl())
                    .codeNumber(user.getCodeNumber())
                    .createdAt(user.getCreatedAt())
                    .updatedAt(user.getUpdatedAt())
                    .role(user.getRole())
                    .gender(user.getGender())
                    .address(user.getAddress())
                    .phoneNumber(user.getPhoneNumber())
                    .dateOfBirth(user.getDateOfBirth())
                    .build();
            case TEACHER -> TeacherResponse.builder()
                    .userId(user.getUserId())
                    .email(user.getEmail())
                    .firstName(user.getFirstName())
                    .middleName(user.getMiddleName())
                    .lastName(user.getLastName())
                    .avatarUrl(user.getAvatarUrl())
                    .codeNumber(user.getCodeNumber())
                    .createdAt(user.getCreatedAt())
                    .updatedAt(user.getUpdatedAt())
                    .role(user.getRole())
                    .gender(user.getGender())
                    .address(user.getAddress())
                    .phoneNumber(user.getPhoneNumber())
                    .dateOfBirth(user.getDateOfBirth())
                    .subjects(((Teacher) user).getSubjects())
                    .build();
            case STUDENT -> StudentResponse.builder()
                    .userId(user.getUserId())
                    .email(user.getEmail())
                    .firstName(user.getFirstName())
                    .middleName(user.getMiddleName())
                    .lastName(user.getLastName())
                    .avatarUrl(user.getAvatarUrl())
                    .codeNumber(user.getCodeNumber())
                    .createdAt(user.getCreatedAt())
                    .updatedAt(user.getUpdatedAt())
                    .dateOfBirth(user.getDateOfBirth())
                    .address(user.getAddress())
                    .phoneNumber(user.getPhoneNumber())
                    .role(user.getRole())
                    .gender(user.getGender())
                    .parents(userRepository.findParentByStudentId(user.getUserId()))
                    .build();
            case PARENT -> ParentResponse.builder()
                    .userId(user.getUserId())
                    .email(user.getEmail())
                    .firstName(user.getFirstName())
                    .middleName(user.getMiddleName())
                    .lastName(user.getLastName())
                    .avatarUrl(user.getAvatarUrl())
                    .codeNumber(user.getCodeNumber())
                    .createdAt(user.getCreatedAt())
                    .updatedAt(user.getUpdatedAt())
                    .dateOfBirth(user.getDateOfBirth())
                    .address(user.getAddress())
                    .phoneNumber(user.getPhoneNumber())
                    .role(user.getRole())
                    .gender(user.getGender())
                    .students(userRepository.findStudentByParentId(user.getUserId()))
                    .build();
            default -> throw new IllegalArgumentException("Vai trò không hợp lệ");
        };

    }
    @Override
    public void updateUser(Long userId, UserUpdateRequest userUpdateRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserIdNotFoundException("Id người dùng không tồn tại"));
        user.setAddress(userUpdateRequest.getAddress());
        user.setAvatarUrl(userUpdateRequest.getAvatarUrl());
        user.setDateOfBirth(userUpdateRequest.getDateOfBirth());
        user.setPhoneNumber(userUpdateRequest.getPhoneNumber());
        user.setGender(userUpdateRequest.getGender());
        if(user.getRole() == Role.TEACHER && ((TeacherUpdateRequest) userUpdateRequest).getSubjects() != null && !((TeacherUpdateRequest) userUpdateRequest).getSubjects().isEmpty()) {
            ((Teacher) user).setSubjects(((TeacherUpdateRequest) userUpdateRequest).getSubjects());
        }
        userRepository.save(user);
    }

    

    private boolean isMiddleNameValid(String middleName) {
        return StringUtils.isNotBlank(middleName);
    }

    private String genCodeNumber(Role role) {
        CodeNumber codeNumber = codeNumberService.getCodeNumberByRole(role);
        codeNumber.setCurrentNumber(codeNumber.getCurrentNumber() + 1);
        codeNumberService.saveCodeNumber(codeNumber);
        return switch (role) {
            case ADMIN, ASSISTANT, TEACHER -> String.format("%04d", codeNumber.getCurrentNumber());
            case STUDENT -> String.format("%s%04d", Year.now(), codeNumber.getCurrentNumber());
            default -> null;
        };
    }

    private String genEmail(String firstName, String middleName, String lastName, String codeNumber, String code) {
        String lastNameWithoutAccents = VietnameseUtils.removeVietnameseAccents(lastName);
        return lastNameWithoutAccents.substring(0, 1).toUpperCase()
                + lastNameWithoutAccents.substring(1).toLowerCase() + "."
                + firstName.toUpperCase().charAt(0)
                + (isMiddleNameValid(middleName) ? middleName.toUpperCase().charAt(0) : "") + "."
                + code
                + codeNumber
                + EMAIL_LAST_DOMAIN;
    }

    private String registerParent(ParentRegisterRequest registerRequest, Student student) {
        if (registerRequest.getRole() != Role.PARENT) {
            throw new IllegalArgumentException("Vai trò không hợp lệ");
        }
        Parent parent = userRepository
                .findParentByFirstNameAndMiddleNameAndLastNameAndPhoneNumber(registerRequest.getFirstName(),
                        registerRequest.getMiddleName(), registerRequest.getLastName(),
                        registerRequest.getPhoneNumber())
                .orElseGet(() -> {
                    Parent p = Parent.builder()
                            .firstName(registerRequest.getFirstName())
                            .middleName(registerRequest.getMiddleName())
                            .lastName(registerRequest.getLastName())
                            .password(registerRequest.getPassword())
                            .role(registerRequest.getRole())
                            .gender(registerRequest.getGender())
                            .codeNumber(registerRequest.getPhoneNumber())
                            .phoneNumber(registerRequest.getPhoneNumber())
                            .dateOfBirth(registerRequest.getDateOfBirth())
                            .email(genEmail(registerRequest.getFirstName(), registerRequest.getMiddleName(),
                                    registerRequest.getLastName(), registerRequest.getPhoneNumber(), registerRequest.getRole().getCode()))
                            .build();
                    return userRepository.save(p);
                });

        parentStudentService.addParentToStudent(parent, student, registerRequest.getRelationship());
        return parent.getEmail();
    }
    
}
 