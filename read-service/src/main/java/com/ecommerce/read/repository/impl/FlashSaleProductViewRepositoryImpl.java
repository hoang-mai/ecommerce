package com.ecommerce.read.repository.impl;

import com.ecommerce.library.utils.FnCommon;
import com.ecommerce.read.dto.FlashSaleStatisticDTO;
import com.ecommerce.read.entity.FlashSaleProductView;
import org.bson.Document;
import org.springframework.data.domain.Sort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class FlashSaleProductViewRepositoryImpl {

    private final MongoTemplate mongoTemplate;

    /**
     * Lấy danh sách flash sale products với phân trang và bộ lọc
     *
     * @param flashSaleCampaignId ID của flash sale campaign (optional)
     * @param shopId              ID của shop (optional)
     * @param ownerId             ID của owner (optional)
     * @param pageable            Thông tin phân trang và sắp xếp
     * @return Page của FlashSaleProductView
     */
    public Page<FlashSaleProductView> getFlashSaleProducts(
            String flashSaleCampaignId,
            String shopId,
            String ownerId,
            Pageable pageable) {
        List<Criteria> criteriaList = new ArrayList<>();

        if (FnCommon.isNotNullOrEmpty(flashSaleCampaignId)) {
            criteriaList.add(Criteria.where("flashSaleCampaignId").is(flashSaleCampaignId));
        }

        if (FnCommon.isNotNullOrEmpty(shopId)) {
            criteriaList.add(Criteria.where("shopId").is(shopId));
        }

        if (FnCommon.isNotNullOrEmpty(ownerId)) {
            criteriaList.add(Criteria.where("ownerId").is(ownerId));
        }

        Criteria finalCriteria = new Criteria();
        if (!criteriaList.isEmpty()) {
            finalCriteria = finalCriteria.andOperator(criteriaList.toArray(new Criteria[0]));
        }

        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(finalCriteria),
                Aggregation.sort(Sort.Direction.DESC, "score"),
                Aggregation.group("productId").first("$$ROOT").as("result"),
                Aggregation.replaceRoot("result"),
                Aggregation.sort(Sort.Direction.DESC, "score"),
                Aggregation.facet()
                        .and(Aggregation.count().as("count")).as("total")
                        .and(Aggregation.skip(pageable.getOffset()), Aggregation.limit(pageable.getPageSize()))
                        .as("data"));

        AggregationResults<Document> results = mongoTemplate.aggregate(aggregation, FlashSaleProductView.class,
                Document.class);
        Document result = results.getUniqueMappedResult();

        List<FlashSaleProductView> content = new ArrayList<>();
        long total = 0;

        if (result != null) {
            List<Document> data = (List<Document>) result.get("data");
            if (data != null) {
                for (Document doc : data) {
                    content.add(mongoTemplate.getConverter().read(FlashSaleProductView.class, doc));
                }
            }

            List<Document> totalList = (List<Document>) result.get("total");
            if (totalList != null && !totalList.isEmpty()) {
                Number count = totalList.get(0).get("count", Number.class);
                if (count != null) {
                    total = count.longValue();
                }
            }
        }

        return new PageImpl<>(content, pageable, total);
    }

    /**
     * Lấy danh sách flash sale products hiện tại (đang diễn ra)
     * Sắp xếp theo score giảm dần
     *
     * @param pageable Thông tin phân trang và sắp xếp
     * @return Page của FlashSaleProductView
     */
    public Page<FlashSaleProductView> getCurrentFlashSaleProducts(Pageable pageable) {
        Instant now = Instant.now();

        Criteria criteria = new Criteria().andOperator(
                Criteria.where("startTime").lte(now),
                Criteria.where("endTime").gte(now));

        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(criteria),
                Aggregation.sort(Sort.Direction.DESC, "score"),
                Aggregation.group("productId").first("$$ROOT").as("result"),
                Aggregation.replaceRoot("result"),
                Aggregation.sort(Sort.Direction.DESC, "score"),
                Aggregation.facet()
                        .and(Aggregation.count().as("count")).as("total")
                        .and(Aggregation.skip(pageable.getOffset()), Aggregation.limit(pageable.getPageSize()))
                        .as("data"));

        AggregationResults<Document> results = mongoTemplate.aggregate(aggregation, FlashSaleProductView.class,
                Document.class);
        Document result = results.getUniqueMappedResult();

        List<FlashSaleProductView> content = new ArrayList<>();
        long total = 0;

        if (result != null) {
            List<Document> data = (List<Document>) result.get("data");
            if (data != null) {
                for (Document doc : data) {
                    content.add(mongoTemplate.getConverter().read(FlashSaleProductView.class, doc));
                }
            }

            List<Document> totalList = (List<Document>) result.get("total");
            if (totalList != null && !totalList.isEmpty()) {
                Number count = totalList.get(0).get("count", Number.class);
                if (count != null) {
                    total = count.longValue();
                }
            }
        }

        return new PageImpl<>(content, pageable, total);
    }

    public FlashSaleStatisticDTO getFlashSaleProductStatistics(String flashSaleCampaignId, Long ownerId) {
        Criteria matchCriteria = Criteria.where("flashSaleCampaignId").is(flashSaleCampaignId);
        if (ownerId != null) {
            matchCriteria = matchCriteria.and("ownerId").is(ownerId.toString());
        }

        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(matchCriteria),

                Aggregation.project()
                        .andInclude("soldQuantity", "totalQuantity", "salePrice",
                                "flashSaleCampaignId", "flashSaleCampaignName", "startTime", "endTime", "totalRevenue")
                        .and(ConvertOperators.ToDouble.toDouble("$totalRevenue")).as("totalRevenueNum"),

                Aggregation.group()
                        .first("flashSaleCampaignId").as("flashSaleCampaignId")
                        .first("flashSaleCampaignName").as("flashSaleCampaignName")
                        .first("startTime").as("startTime")
                        .first("endTime").as("endTime")
                        .sum("soldQuantity").as("totalSoldQuantity")
                        .sum("totalQuantity").as("totalQuantity")
                        .sum("totalRevenueNum").as("totalRevenue"),

                Aggregation.project()
                        .andInclude("flashSaleCampaignId", "flashSaleCampaignName",
                                "startTime", "endTime", "totalQuantity",
                                "totalSoldQuantity", "totalRevenue")
                        .and(ConditionalOperators.when(Criteria.where("totalQuantity").gt(0))
                                .then(ArithmeticOperators.Multiply.valueOf(
                                        ArithmeticOperators.Divide.valueOf("totalSoldQuantity")
                                                .divideBy("totalQuantity"))
                                        .multiplyBy(100))
                                .otherwise(0))
                        .as("soldRate"));

        AggregationResults<FlashSaleStatisticDTO> results = mongoTemplate.aggregate(
                aggregation,
                FlashSaleProductView.class,
                FlashSaleStatisticDTO.class);

        FlashSaleStatisticDTO result = results.getUniqueMappedResult();
        if (result == null) {
            return FlashSaleStatisticDTO.builder()
                    .flashSaleCampaignId(flashSaleCampaignId)
                    .flashSaleCampaignName(null)
                    .startTime(null)
                    .endTime(null)
                    .totalQuantity(0L)
                    .totalSoldQuantity(0L)
                    .soldRate(0.0)
                    .totalRevenue(BigDecimal.ZERO)
                    .build();
        }

        return result;
    }
}