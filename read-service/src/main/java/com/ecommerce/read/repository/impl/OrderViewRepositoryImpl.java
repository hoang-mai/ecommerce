package com.ecommerce.read.repository.impl;

import com.ecommerce.library.enumeration.OrderStatus;
import com.ecommerce.library.utils.FnCommon;
import com.ecommerce.read.entity.OrderView;
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
public class OrderViewRepositoryImpl {
    private final MongoTemplate mongoTemplate;

    public Page<OrderView> getOrderView(String shopId, Long currentUserId, OrderStatus orderStatus, String keyword, Pageable pageable) {
        List<Criteria> criteriaList = new ArrayList<>();
        if(FnCommon.isNotNullOrEmpty(shopId)){
            criteriaList.add(Criteria.where("shopId").is(shopId));
        }else{
            criteriaList.add(Criteria.where("userId").is(String.valueOf(currentUserId)));
        }

        if(FnCommon.isNotNull(orderStatus)){
            criteriaList.add(Criteria.where("orderItems.orderStatus").is(orderStatus));
        }
        if (FnCommon.isNotNullOrEmpty(keyword)) {
            criteriaList.add(new Criteria().orOperator(
                    Criteria.where("receiverName").regex(keyword, "i"),
                    Criteria.where("address").regex(keyword, "i"),
                    Criteria.where("phoneNumber").regex(keyword, "i"),
                    Criteria.where("orderItems.productName").regex(keyword, "i")
            ));
        }
        Criteria finalCriteria = new Criteria().andOperator(criteriaList.toArray(new Criteria[0]));
        Query query = new Query(finalCriteria);
        long total = mongoTemplate.count(query, OrderView.class);
        query.with(pageable);
        List<OrderView> orderViews = mongoTemplate.find(query, OrderView.class);
        return new PageImpl<>(orderViews, pageable, total);
    }
}
