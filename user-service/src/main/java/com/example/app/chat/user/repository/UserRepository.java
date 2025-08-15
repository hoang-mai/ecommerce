package com.example.app.chat.user.repository;

import com.example.app.chat.user.dto.user.UserPreviewResponse;
import com.example.app.chat.user.dto.information.ParentResponse;
import com.example.app.chat.user.dto.information.StudentResponse;
import com.example.app.chat.user.entity.Parent;
import com.example.app.chat.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.Set;

public interface UserRepository extends JpaRepository<User, Long> {

}