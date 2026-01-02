package com.ecommerce.read.repository.impl;


import com.ecommerce.library.enumeration.AccountStatus;
import com.ecommerce.library.enumeration.Role;
import com.ecommerce.library.utils.FnCommon;
import com.ecommerce.read.dto.NewShopViewStatisticDTO;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
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

    public List<NewUserViewStatisticDTO> getUserStatisticsByDateRange(LocalDateTime fromDate, LocalDateTime toDate) {
        List<Criteria> criteriaList = new ArrayList<>();
        if (fromDate != null || toDate != null) {
            LocalDateTime start = (fromDate != null)
                ? fromDate.toLocalDate().withDayOfMonth(1).atStartOfDay()
                : LocalDateTime.of(1970, 1, 1, 0, 0);
            LocalDateTime end = (toDate != null)
                ? toDate.toLocalDate().withDayOfMonth(1).plusMonths(1).atStartOfDay()
                : LocalDate.now().withDayOfMonth(1).plusMonths(1).atStartOfDay();
            criteriaList.add(Criteria.where("createdAt").gte(start).lt(end));
        }

        Criteria finalCriteria = new Criteria().andOperator(criteriaList.toArray(new Criteria[0]));
        MatchOperation match = Aggregation.match(finalCriteria);


        ProjectionOperation project = Aggregation.project()
            .and(DateOperators.dateOf("createdAt").toString("%Y-%m")).as("monthString");

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
