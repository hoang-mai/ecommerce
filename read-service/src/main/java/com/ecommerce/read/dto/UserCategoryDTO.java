package com.ecommerce.read.dto;

import com.ecommerce.library.enumeration.UserCategoryType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCategoryDTO {
    private Long categoryId;
    private UserCategoryType userCategoryType;
}
