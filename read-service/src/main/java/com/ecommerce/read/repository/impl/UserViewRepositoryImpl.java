package com.ecommerce.read.repository.impl;


import com.ecommerce.library.enumeration.AccountStatus;
import com.ecommerce.library.enumeration.Role;
import com.ecommerce.library.utils.FnCommon;
import com.ecommerce.read.dto.NewUserViewStatisticDTO;
import com.ecommerce.read.entity.UserView;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
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
                    Criteria.where("fullName").regex(keyword, "i")
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

    public List<NewUserViewStatisticDTO> getUserStatisticsByDateRange(Instant fromDate, Instant toDate) {
        List<Criteria> criteriaList = new ArrayList<>();
        if (fromDate != null || toDate != null) {
            Instant start = (fromDate != null) ? fromDate : Instant.EPOCH;
            Instant end = (toDate != null) ? toDate : Instant.now();
            criteriaList.add(Criteria.where("createdAt").gte(start).lt(end));
        }

        Criteria finalCriteria = new Criteria();
        if(!criteriaList.isEmpty()) {
            finalCriteria = finalCriteria.andOperator(criteriaList.toArray(new Criteria[0]));
        }
        MatchOperation match = Aggregation.match(finalCriteria);


        ProjectionOperation project = Aggregation.project()
            .and(DateOperators.dateOf("createdAt").toString("%Y-%m")
                .withTimezone(DateOperators.Timezone.valueOf("Asia/Ho_Chi_Minh"))).as("monthString");

        GroupOperation group = Aggregation.group("monthString")
            .count().as("newUserViews");


        SortOperation sort =
            Aggregation.sort(Sort.Direction.ASC, "_id");

        Aggregation agg = Aggregation.newAggregation(match, project, group, sort);

        AggregationResults<Document> results = mongoTemplate.aggregate(agg, "user_views", Document.class);

        List<NewUserViewStatisticDTO> statistics = new ArrayList<>();

        results.getMappedResults().forEach(doc -> {
            Object id = doc.get("_id");
            Object count = doc.get("newUserViews");
            if (id != null && count != null) {
                try {
                    String monthString = id.toString();
                    Integer newUserViews = ((Number) count).intValue();
                    statistics.add(NewUserViewStatisticDTO.builder()
                        .localDate(monthString)
                        .newUserViews(newUserViews)
                        .build());
                } catch (Exception ignored) {
                }
            }
        });

        return statistics;
    }
}
