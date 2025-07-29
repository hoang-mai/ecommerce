package com.example.edu.school.auth.client;

import com.example.edu.school.auth.client.dto.ReqCreateUserDTO;
import com.example.edu.school.auth.config.FeignClientConfig;
import com.example.edu.school.library.utils.BaseResponse;
import com.example.edu.school.library.utils.Constant;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "user-service",path = Constant.USER, configuration = FeignClientConfig.class)
public interface UserClient {

    @PostMapping("/create")
    ResponseEntity<BaseResponse<Void>> createUser(@RequestBody ReqCreateUserDTO reqCreateUserDTO);
}
