package com.ecommerce.read.repository.impl;

import com.ecommerce.library.enumeration.OrderStatus;
import com.ecommerce.library.utils.FnCommon;
import com.ecommerce.read.dto.OrderViewStatisticDTO;
import com.ecommerce.read.entity.OrderView;
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
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class OrderViewRepositoryImpl {
    private final MongoTemplate mongoTemplate;

    public Page<OrderView> getOrderView(String shopId,Boolean isOwner, Long currentUserId, OrderStatus orderStatus, String keyword, String productId, Pageable pageable) {
        List<Criteria> criteriaList = new ArrayList<>();
        if(FnCommon.isNotNullOrEmpty(shopId)){
            criteriaList.add(Criteria.where("shopId").is(shopId));
            criteriaList.add(Criteria.where("ownerId").is(String.valueOf(currentUserId)));
        }else if(Boolean.TRUE.equals(isOwner)){
            criteriaList.add(Criteria.where("ownerId").is(String.valueOf(currentUserId)));
        }else {
            criteriaList.add(Criteria.where("userId").is(String.valueOf(currentUserId)));
        }

        if(FnCommon.isNotNull(orderStatus)){
            criteriaList.add(Criteria.where("orderStatus").is(orderStatus));
        }
        if (FnCommon.isNotNullOrEmpty(keyword)) {
            criteriaList.add(new Criteria().orOperator(
                    Criteria.where("receiverName").regex(keyword, "i"),
                    Criteria.where("address").regex(keyword, "i"),
                    Criteria.where("phoneNumber").regex(keyword, "i"),
                    Criteria.where("orderItems.productName").regex(keyword, "i")
            ));
        }
        if (FnCommon.isNotNullOrEmpty(productId)) {
            criteriaList.add(Criteria.where("orderItems.productId").is(productId));
        }
        Criteria finalCriteria = new Criteria().andOperator(criteriaList.toArray(new Criteria[0]));
        Query query = new Query(finalCriteria);
        long total = mongoTemplate.count(query, OrderView.class);
        query.with(pageable);
        List<OrderView> orderViews = mongoTemplate.find(query, OrderView.class);
        return new PageImpl<>(orderViews, pageable, total);
    }


    public Map<OrderStatus, Long> getOrderCountByStatus(String shopId, Boolean isOwner, Long currentUserId, Integer month, Integer year) {
        List<Criteria> criteriaList = new ArrayList<>();
        if(FnCommon.isNotNullOrEmpty(shopId)){
            criteriaList.add(Criteria.where("shopId").is(shopId));
            criteriaList.add(Criteria.where("ownerId").is(String.valueOf(currentUserId)));
        } else if(Boolean.TRUE.equals(isOwner)){
            criteriaList.add(Criteria.where("ownerId").is(String.valueOf(currentUserId)));
        } else {
            criteriaList.add(Criteria.where("userId").is(String.valueOf(currentUserId)));
        }

        if (month != null) {
            int useYear = (year != null) ? year : LocalDate.now().getYear();
            LocalDateTime start = LocalDate.of(useYear, month, 1).atStartOfDay();
            LocalDateTime next = start.plusMonths(1);
            criteriaList.add(Criteria.where("createdAt").gte(start).lt(next));
        }

        Criteria finalCriteria = new Criteria().andOperator(criteriaList.toArray(new Criteria[0]));
        MatchOperation match = Aggregation.match(finalCriteria);
        GroupOperation group = Aggregation.group("orderStatus").count().as("count");
        Aggregation agg = Aggregation.newAggregation(match, group);

        AggregationResults<Document> results = mongoTemplate.aggregate(agg, "order_views", Document.class);

        Map<OrderStatus, Long> map = new EnumMap<>(OrderStatus.class);
        for (OrderStatus status : OrderStatus.values()) {
            map.put(status, 0L);
        }

        results.getMappedResults().forEach(doc -> {
            Object id = doc.get("_id");
            Object count = doc.get("count");
            if (id != null && count != null) {
                try {
                    String statusStr = id.toString();
                    OrderStatus status = OrderStatus.valueOf(statusStr);
                    long c = ((Number) count).longValue();
                    map.put(status, c);
                } catch (IllegalArgumentException ignored) {
                }
            }
        });

        return map;
    }


    /**
     * Thống kê số lượng đơn hàng tạo mới trong mỗi ngày theo khoảng thời gian
     * @param shopId ID cửa hàng (optional)
     * @param isOwner Có phải owner không
     * @param currentUserId ID người dùng hiện tại
     * @param fromDate Ngày bắt đầu (optional)
     * @param toDate Ngày kết thúc (optional)
     * @return List thống kê theo ngày
     */
    public List<OrderViewStatisticDTO> getOrderStatisticsByDateRange(
            String shopId, Boolean isOwner, Long currentUserId, LocalDateTime fromDate, LocalDateTime toDate) {

        List<Criteria> criteriaList = new ArrayList<>();


        if(FnCommon.isNotNullOrEmpty(shopId)){
            criteriaList.add(Criteria.where("shopId").is(shopId));
            criteriaList.add(Criteria.where("ownerId").is(String.valueOf(currentUserId)));
        } else if(Boolean.TRUE.equals(isOwner)){
            criteriaList.add(Criteria.where("ownerId").is(String.valueOf(currentUserId)));
        } else {
            criteriaList.add(Criteria.where("userId").is(String.valueOf(currentUserId)));
        }


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
                .and(DateOperators.dateOf("createdAt").toString("%Y-%m")).as("monthString")
                .andInclude("shopId", "ownerId", "userId");

        GroupOperation group = Aggregation.group("monthString")
                .count().as("newOrders");


        SortOperation sort =
                Aggregation.sort(Sort.Direction.ASC, "_id");

        Aggregation agg = Aggregation.newAggregation(match, project, group, sort);

        AggregationResults<Document> results = mongoTemplate.aggregate(agg, "order_views", Document.class);

        List<OrderViewStatisticDTO> statistics = new ArrayList<>();

        results.getMappedResults().forEach(doc -> {
            Object id = doc.get("_id");
            Object count = doc.get("newOrders");
            if (id != null && count != null) {
                try {
                    String monthString = id.toString();
                    Integer orderCount = ((Number) count).intValue();
                    statistics.add(OrderViewStatisticDTO.builder()
                            .localDate(monthString)
                            .newOrders(orderCount)
                            .build());
                } catch (Exception ignored) {
                }
            }
        });

        return statistics;
    }
}

