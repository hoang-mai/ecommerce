package com.ecommerce.read.repository.impl;


import com.ecommerce.library.enumeration.AccountStatus;
import com.ecommerce.library.enumeration.Role;
import com.ecommerce.library.utils.FnCommon;
import com.ecommerce.read.entity.UserView;
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
public class UserViewRepositoryImpl  {

    private final MongoTemplate mongoTemplate;

    public Page<UserView> getUserView(AccountStatus accountStatus, Role role, String keyword, Pageable pageable){
        List<Criteria> criteriaList = new ArrayList<>();
        if(FnCommon.isNotNull(accountStatus)){
            criteriaList.add(Criteria.where("accountStatus").is(accountStatus));
        }
        if(FnCommon.isNotNull(role)) {
            criteriaList.add(Criteria.where("role").is(role));
        }
        if(FnCommon.isNotNullOrEmpty(keyword)){
            criteriaList.add(new Criteria().orOperator(
                    Criteria.where("username").regex(keyword, "i"),
                    Criteria.where("email").regex(keyword, "i"),
                    Criteria.where("firstName").regex(keyword, "i"),
                    Criteria.where("middleName").regex(keyword, "i"),
                    Criteria.where("lastName").regex(keyword, "i")
            ));
        }
        Criteria finalCriteria = new Criteria();
        if(!criteriaList.isEmpty()) {
            finalCriteria = finalCriteria.andOperator(criteriaList.toArray(new Criteria[0]));
        }
        Query query = new Query(finalCriteria);
        long total = mongoTemplate.count(query, UserView.class);
        query.with(pageable);
        List<UserView> userViews = mongoTemplate.find(query, UserView.class);
        return new PageImpl<>(userViews, pageable, total);
    }
}
