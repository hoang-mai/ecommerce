package com.ecommerce.read.repository.impl;

import com.ecommerce.library.utils.FnCommon;
import com.ecommerce.read.entity.ReviewView;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ReviewViewRepositoryImpl {
    private final MongoTemplate mongoTemplate;
    public Page<ReviewView> getReviewsByProductId(Long productId, String stars, Long ownerId, Long shopId, Boolean isReply, Pageable pageable) {
        List<Criteria> criteriaList = new ArrayList<>();
        if(FnCommon.isNotNull(productId)){
            criteriaList.add(Criteria.where("productId").is(String.valueOf(productId)));
        }
        if(FnCommon.isNotNull(stars)){
            criteriaList.add(Criteria.where("rating").is(stars));
        }
        if(FnCommon.isNotNull(ownerId)){
            criteriaList.add(Criteria.where("ownerId").is(String.valueOf(ownerId)));
        }
        if(FnCommon.isNotNull(shopId)){
            criteriaList.add(Criteria.where("shopId").is(String.valueOf(shopId)));
        }
        if (FnCommon.isNotNull(isReply)) {
            if (isReply) {
                criteriaList.add(Criteria.where("reviewReplyView").ne(null));
            } else {
                criteriaList.add(Criteria.where("reviewReplyView").is(null));
            }
        }
        Criteria finalCriteria =  new Criteria();
        if(!criteriaList.isEmpty()){
            finalCriteria = new Criteria().andOperator(criteriaList.toArray(new Criteria[0]));
        }
        Query query = new Query(finalCriteria);
        long total = mongoTemplate.count(query, ReviewView.class);
        query.with(pageable);
        List<ReviewView> reviews = mongoTemplate.find(query, ReviewView.class);
        return new PageImpl<>(reviews, pageable, total);

    }

    public void updateAvatarUserInReviews(String userId, String avatarUrl) {
        Query query = new Query(Criteria.where("userId").is(userId));
        Update update = new Update().set("avatarUrl", avatarUrl);
        mongoTemplate.updateMulti(query, update, ReviewView.class);
    }

    public void updateUserInReviews(String userId, String fullName) {
        Query query = new Query(Criteria.where("userId").is(userId));
        Update update = new Update().set("fullName", fullName);
        mongoTemplate.updateMulti(query, update, ReviewView.class);
    }
}
