package com.ecommerce.read.repository.impl;

import com.ecommerce.library.enumeration.ShopStatus;
import com.ecommerce.library.kafka.event.review.CreateReviewViewEvent;
import com.ecommerce.read.entity.ShopView;
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
public class ShopViewRepositoryImpl {
    private final MongoTemplate mongoTemplate;

    public Page<ShopView> getShopsByCurrentOwner(String ownerId, ShopStatus status, String keyword, Pageable pageable) {
        List<Criteria> criteriaList = new ArrayList<>();
        criteriaList.add(Criteria.where("ownerId").is(ownerId));
        if (status != null) {
            criteriaList.add(Criteria.where("shopStatus").is(status));
        }
        if (keyword != null && !keyword.isEmpty()) {
            criteriaList.add(Criteria.where("shopName").regex(keyword, "i"));
            criteriaList.add(Criteria.where("description").regex(keyword, "i"));
            criteriaList.add(Criteria.where("phoneNumber").regex(keyword, "i"));
            criteriaList.add(Criteria.where("detail").regex(keyword, "i"));
        }
        Criteria finalCriteria = new Criteria();
        finalCriteria = finalCriteria.andOperator(criteriaList.toArray(new Criteria[0]));
        Query query = new Query(finalCriteria);
        long total = mongoTemplate.count(query, ShopView.class);
        query.with(pageable);
        List<ShopView> shopViews = mongoTemplate.find(query, ShopView.class);
        return new PageImpl<>(shopViews, pageable, total);
    }

    public Page<ShopView> searchShops(ShopStatus status, String keyword, Pageable pageable) {
        List<Criteria> criteriaList = new ArrayList<>();
        if (status != null) {
            criteriaList.add(Criteria.where("shopStatus").is(status));
        }
        if (keyword != null && !keyword.isEmpty()) {
            criteriaList.add(Criteria.where("shopName").regex(keyword, "i"));
            criteriaList.add(Criteria.where("description").regex(keyword, "i"));
            criteriaList.add(Criteria.where("phoneNumber").regex(keyword, "i"));
            criteriaList.add(Criteria.where("detail").regex(keyword, "i"));
        }
        Criteria finalCriteria = new Criteria();
        if (!criteriaList.isEmpty()) {
            finalCriteria = finalCriteria.andOperator(criteriaList.toArray(new Criteria[0]));
        }
        Query query = new Query(finalCriteria);
        long total = mongoTemplate.count(query, ShopView.class);
        query.with(pageable);
        List<ShopView> shopViews = mongoTemplate.find(query, ShopView.class);
        return new PageImpl<>(shopViews, pageable, total);
    }

    public void updateRating(String shopId, Double rating, Boolean isUpdate, Double oldRating, Boolean isDelete) {
        Query query = new Query(Criteria.where("_id").is(shopId));
        Update update;
        if (Boolean.TRUE.equals(isDelete)) {
            update = new Update()
                .inc("numberOfReviews", -1)
                .inc("rating", -oldRating);
        } else if (Boolean.TRUE.equals(isUpdate)) {
            update = new Update()
                .inc("rating", rating - oldRating);
        } else {
            update = new Update()
                .inc("numberOfReviews", 1)
                .inc("rating", rating);
        }
        mongoTemplate.updateFirst(query, update, ShopView.class);
    }
}
