package com.example.edu.school.user.controller;

import com.example.edu.school.user.dto.request.update.ParentRelationshipUpdateRequest;
import com.example.edu.school.user.dto.request.update.UserUpdateRequest;
import com.example.edu.school.user.dto.request.register.ParentRegisterRequest;
import com.example.edu.school.user.dto.request.register.RegisterRequest;
import com.example.edu.school.user.dto.response.BaseResponse;
import com.example.edu.school.user.dto.response.PageResponse;
import com.example.edu.school.user.dto.response.UserCredentialResponse;
import com.example.edu.school.user.dto.response.UserCredentialResponse;
import com.example.edu.school.user.dto.response.UserPreviewResponse;
import com.example.edu.school.user.dto.response.information.UserResponse;
import com.example.edu.school.user.model.Role;
import com.example.edu.school.user.service.UserService;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Slf4j
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private static final String REGISTER_SUCCESS_MESSAGE = "Đăng ký thành công";
    private static final String GET_USER_SUCCESS_MESSAGE = "Lấy thông tin người dùng thành công";

    @GetMapping("/login/{email}")
    public ResponseEntity<BaseResponse<UserCredentialResponse>> getUserCredentialByEmail(@PathVariable String email){
        log.info("Fetching user by email: {}", email);
        UserCredentialResponse userCredentialResponse = userService.getUserCredentialByEmail(email);
        log.info("User fetched successfully: {}", userCredentialResponse);
        return ResponseEntity.ok(BaseResponse.<UserCredentialResponse>builder()
                .statusCode(HttpStatus.OK.value())
                .message(GET_USER_SUCCESS_MESSAGE)
                .data(userCredentialResponse)
                .build());
    }

    @PostMapping("/register")
    public ResponseEntity<BaseResponse<String>> register(@Valid @RequestBody RegisterRequest registerRequest) {
        log.info("Registering user: {}", registerRequest);
        String email = userService.register(registerRequest);
        log.info("User registered successfully: {}", email);
        return ResponseEntity.status(HttpStatus.CREATED).body(BaseResponse.<String>builder()
                .statusCode(HttpStatus.CREATED.value())
                .message(REGISTER_SUCCESS_MESSAGE)
                .data(email)
                .build());
    }
    @PostMapping("/register-parent")
    public ResponseEntity<BaseResponse<String>> registerParent(@Valid @RequestBody ParentRegisterRequest registerRequest, @RequestParam @JsonProperty("student_id") Long studentId){
        log.info("Registering parent: {}", registerRequest);
        String email = userService.registerParent(registerRequest, studentId);
        log.info("Parent registered successfully: {}", email);
        return ResponseEntity.status(HttpStatus.CREATED).body(BaseResponse.<String>builder()
                .statusCode(HttpStatus.CREATED.value())
                .message(REGISTER_SUCCESS_MESSAGE)
                .data(email)
                .build());
    }

    @GetMapping("/{userId}")
    public ResponseEntity<BaseResponse<UserResponse>> getUserById(@PathVariable Long userId) {
        log.info("Fetching user by id: {}", userId);
        UserResponse userResponse = userService.getUserById(userId);
        log.info("User fetched successfully: {}", userResponse);
        return ResponseEntity.ok(BaseResponse.<UserResponse>builder()
                .statusCode(HttpStatus.OK.value())
                .message(GET_USER_SUCCESS_MESSAGE)
                .data(userResponse) 
                .build());
    }
    @PatchMapping("/{userId}")
    public ResponseEntity<BaseResponse<Void>> updateUser(@PathVariable Long userId, @RequestBody UserUpdateRequest userUpdateRequest) {
        log.info("Updating user with id: {}", userId);
        userService.updateUser(userId, userUpdateRequest);
        log.info("User updated successfully: {}", userId);
        return ResponseEntity.ok(BaseResponse.<Void>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Cập nhật thông tin người dùng thành công")
                .build());
    }

    @PatchMapping("/update-parent-relationship")
    public ResponseEntity<BaseResponse<Void>> updateParentRelationship(@RequestBody ParentRelationshipUpdateRequest parentRelationshipUpdateRequest) {
        log.info("Updating parent relationship: {}", parentRelationshipUpdateRequest);
        userService.updateParentRelationship(parentRelationshipUpdateRequest);
        log.info("Parent relationship updated successfully: {}", parentRelationshipUpdateRequest);
        return ResponseEntity.ok(BaseResponse.<Void>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Cập nhật mối quan hệ giữa phụ huynh và học sinh thành công")
                .build());
    }
    @GetMapping("/search")
    public ResponseEntity<BaseResponse<PageResponse<List<UserPreviewResponse>>>> searchUsers(
            @RequestParam(value = "page_no", defaultValue = "0") int page,
            @RequestParam(value = "page_size", defaultValue = "5") int size,
            @RequestParam(value = "query") String query
    ) {
        log.info("Searching users with query: {}", query);
        PageResponse<List<UserPreviewResponse>> userPreviewResponsePageResponse = userService.searchUsers(page, size, query);
        log.info("User search results: {}", userPreviewResponsePageResponse);
        return ResponseEntity.ok(BaseResponse.<PageResponse<List<UserPreviewResponse>>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Tìm kiếm người dùng thành công")
                .data(userPreviewResponsePageResponse)
                .build());
    }
    @GetMapping()
    public ResponseEntity<BaseResponse<PageResponse<List<UserPreviewResponse>>>> getUsers(
            @RequestParam(value = "page_no", defaultValue = "0") int page,
            @RequestParam(value = "page_size", defaultValue = "20") int size,
            @RequestParam(value = "role",required = false) Role role
    ){
        log.info("Searching users with query: {}", role);
        PageResponse<List<UserPreviewResponse>> userPreviewResponsePageResponse = userService.getUsers(page, size, role);
        log.info("User search results: {}", userPreviewResponsePageResponse);
        return ResponseEntity.ok(BaseResponse.<PageResponse<List<UserPreviewResponse>>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Tìm kiếm người dùng thành công")
                .data(userPreviewResponsePageResponse)
                .build());
    }

}
