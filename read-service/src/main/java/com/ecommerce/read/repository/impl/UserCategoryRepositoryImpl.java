package com.ecommerce.read.repository.impl;

import com.ecommerce.read.entity.UserCategoryScore;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserCategoryRepositoryImpl {
    private final MongoTemplate mongoTemplate;

    public UserCategoryScore findById(String id) {
        return mongoTemplate.findById(id, UserCategoryScore.class);
    }

    public void save(UserCategoryScore userCategoryScore) {
        mongoTemplate.save(userCategoryScore);
    }
}
