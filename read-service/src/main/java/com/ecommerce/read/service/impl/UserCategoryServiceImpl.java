package com.ecommerce.read.service.impl;

import com.ecommerce.library.component.UserHelper;
import com.ecommerce.read.dto.UserCategoryDTO;
import com.ecommerce.read.entity.UserCategoryScore;
import com.ecommerce.read.repository.impl.UserCategoryRepositoryImpl;
import com.ecommerce.read.service.UserCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserCategoryServiceImpl implements UserCategoryService {
    private final UserHelper userHelper;
    private final UserCategoryRepositoryImpl userCategoryRepositoryImpl;
    @Override
    public void addUserCategory(UserCategoryDTO userCategoryDTO) {
        Long userId = userHelper.getCurrentUserId();
        save(userId, userCategoryDTO);
    }

    @Override
    public void addUserCategoryByUserId(Long userId, UserCategoryDTO userCategoryDTO) {
        save(userId, userCategoryDTO);
    }

    private void save(Long userId, UserCategoryDTO userCategoryDTO) {
        UserCategoryScore userCategoryScore = userCategoryRepositoryImpl.findById(String.valueOf(userId));
        if (userCategoryScore != null) {
            userCategoryScore.addCategoryScore(String.valueOf(userCategoryDTO.getCategoryId()),userCategoryDTO.getUserCategoryType().getWeight());
        } else {
            userCategoryScore = new UserCategoryScore();
            userCategoryScore.set_id(String.valueOf(userId));
            userCategoryScore.addCategoryScore(String.valueOf(userCategoryDTO.getCategoryId()),userCategoryDTO.getUserCategoryType().getWeight());
        }
        userCategoryRepositoryImpl.save(userCategoryScore);
    }
}
