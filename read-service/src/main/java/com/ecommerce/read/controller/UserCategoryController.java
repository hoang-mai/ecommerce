package com.ecommerce.read.controller;

import com.ecommerce.library.component.MessageService;
import com.ecommerce.library.enumeration.UserCategoryType;
import com.ecommerce.library.utils.BaseResponse;
import com.ecommerce.library.utils.Constant;
import com.ecommerce.library.utils.MessageSuccess;
import com.ecommerce.read.dto.UserCategoryDTO;
import com.ecommerce.read.service.UserCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = Constant.USER_CATEGORY)
@RequiredArgsConstructor
public class UserCategoryController {

    private final UserCategoryService userCategoryService;
    private final MessageService messageService;

    @PostMapping()
    public ResponseEntity<BaseResponse<Void>> addUserCategory(
        @RequestBody UserCategoryDTO userCategoryDTO
        ) {
        userCategoryService.addUserCategory(userCategoryDTO);
        return ResponseEntity.ok(
            BaseResponse.<Void>builder()
                .statusCode(200)
                .message(messageService.getMessage(MessageSuccess.USER_CATEGORY_ADD_SUCCESS))
                .build()
        );
    }
}
