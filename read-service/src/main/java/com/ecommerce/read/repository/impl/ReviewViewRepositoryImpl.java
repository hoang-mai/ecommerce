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
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ReviewViewRepositoryImpl {
    private final MongoTemplate mongoTemplate;
    public Page<ReviewView> getReviewsByProductId(String productId, Integer stars, Pageable pageable) {
        List<Criteria> criteriaList = new ArrayList<>();

        criteriaList.add(Criteria.where("productId").is(productId));
        if(FnCommon.isNotNull(stars)){
            criteriaList.add(Criteria.where("stars").is(stars));
        }
        Criteria finalCriteria =  new Criteria().andOperator(criteriaList.toArray(new Criteria[0]));
        Query query = new Query(finalCriteria);
        long total = mongoTemplate.count(query, ReviewView.class);
        query.with(pageable);
        List<ReviewView> reviews = mongoTemplate.find(query, ReviewView.class);
        return new PageImpl<>(reviews, pageable, total);

    }
}
