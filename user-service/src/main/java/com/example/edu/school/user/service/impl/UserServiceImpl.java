package com.example.edu.school.user.service.impl;

import com.example.edu.school.library.exception.NotFoundException;
import com.example.edu.school.library.utils.Constant;
import com.example.edu.school.library.utils.FnCommon;
import com.example.edu.school.user.dto.update.TeacherUpdateRequest;
import com.example.edu.school.user.dto.update.UserUpdateRequest;
import com.example.edu.school.user.dto.user.ReqCreateUserDTO;
import com.example.edu.school.user.saga.data.CreateUserData;
import com.example.edu.school.user.saga.workflow.CreateUserWorkFlow;
import com.example.edu.school.user.service.ParentStudentService;
import com.example.edu.school.user.service.UserService;
import com.example.edu.school.library.utils.PageResponse;
import com.example.edu.school.user.dto.user.UserPreviewResponse;
import com.example.edu.school.user.dto.information.UserResponse;
import com.example.edu.school.library.enumeration.Role;
import com.example.edu.school.user.entity.*;
import com.example.edu.school.user.repository.UserRepository;
import io.temporal.api.common.v1.WorkflowExecution;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.client.WorkflowStub;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final WorkflowClient workflowClient;
    private final UserRepository userRepository;
    private final ParentStudentService parentStudentService;

    @Override
    public String createUser(ReqCreateUserDTO reqCreateUserDTO) {
        WorkflowOptions options = WorkflowOptions.newBuilder().setTaskQueue(Constant.CREATE_USER_QUEUE).build();
        CreateUserWorkFlow createUserWorkFlow = workflowClient.newWorkflowStub(CreateUserWorkFlow.class, options);

        WorkflowExecution workflowExecution= WorkflowClient.start(createUserWorkFlow::createUser,CreateUserData.builder()
                .password(reqCreateUserDTO.getPassword())
                .firstName(reqCreateUserDTO.getFirstName())
                .middleName(reqCreateUserDTO.getMiddleName())
                .lastName(reqCreateUserDTO.getLastName())
                .role(reqCreateUserDTO.getRole())
                .phoneNumber(reqCreateUserDTO.getPhoneNumber())
                .address(reqCreateUserDTO.getAddress())
                .gender(reqCreateUserDTO.getGender())
                .dateOfBirth(reqCreateUserDTO.getDateOfBirth())
                .build());
        WorkflowStub untypedStub = workflowClient.newUntypedWorkflowStub(workflowExecution.getWorkflowId());

        return untypedStub.getResult(String.class);
    }

    @Override
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }

    @Override
    public void saveUser(CreateUserData createUserData) {
        String email = genEmail(createUserData);
        User user = User.builder()
                .firstName(createUserData.getFirstName())
                .middleName(createUserData.getMiddleName())
                .lastName(createUserData.getLastName())
                .role(createUserData.getRole())
                .gender(createUserData.getGender())
                .phoneNumber(createUserData.getPhoneNumber())
                .dateOfBirth(createUserData.getDateOfBirth())
                .address(createUserData.getAddress())
                .email(email)
                .build();
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
        userRepository.save(user);
        createUserData.setEmail(email);
        createUserData.setUserId(user.getUserId());
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

    /**
     * Tạo email từ thông tin tài khoản.
     * Quy tắc tạo email:
     * - Lấy chữ cái đầu tiên của tên (lastName) viết hoa.
     * - Lấy các chữ cái còn lại của tên.
     * - Lấy chữ cái đầu tiên của họ (firstName) viết hoa.
     * - Nếu có tên đệm (middleName), lấy chữ cái đầu tiên viết hoa.
     * - Kết thúc với "@school.edu.vn".
     * Ví dụ: Họ là "Nguyễn", Tên là "Văn", Tên đệm là "A" thì email sẽ là "Van.NA@school.edu.vn".
     *
     * @param createUserData DTO chứa thông tin tài khoản mới
     * @return email được tạo
     */
    private String genEmail(CreateUserData createUserData) {
        String lastNameWithoutAccents = FnCommon.removeVietnameseAccents(createUserData.getLastName());
        return lastNameWithoutAccents.substring(0, 1).toUpperCase()
                + lastNameWithoutAccents.substring(1).toLowerCase() + "."
                + createUserData.getFirstName().toUpperCase().charAt(0)
                + (FnCommon.isNullOrEmpty(createUserData.getMiddleName()) ? createUserData.getMiddleName().toUpperCase().charAt(0) : "")
                + Constant.EMAIL_DOMAIN;
    }

}
 