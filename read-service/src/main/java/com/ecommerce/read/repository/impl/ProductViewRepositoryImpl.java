package com.ecommerce.read.repository.impl;

import com.ecommerce.library.enumeration.ProductStatus;
import com.ecommerce.library.enumeration.RatingNumber;
import com.ecommerce.library.enumeration.ShopStatus;
import com.ecommerce.library.exception.NotFoundException;
import com.ecommerce.library.kafka.event.product.UpdateProductVariantStatusEvent;
import com.ecommerce.library.utils.FnCommon;
import com.ecommerce.library.utils.MessageError;
import com.ecommerce.read.dto.ProductViewStatisticDTO;
import com.ecommerce.read.entity.ProductView;
import com.mongodb.client.result.UpdateResult;
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
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ProductViewRepositoryImpl {

    private final MongoTemplate mongoTemplate;

    public void updateProductVariantStatus(UpdateProductVariantStatusEvent event) {
        Query query = new Query(Criteria.where("_id")
            .is(String.valueOf(event.getProductId()))
            .and("productVariants.productVariantId")
            .is(String.valueOf(event.getProductVariantId())));

        Update update = new Update()
            .set("productVariants.$.productVariantStatus", event.getStatus());

        UpdateResult result = mongoTemplate.updateFirst(query, update, ProductView.class);

        if (result.getMatchedCount() == 0) {
            throw new NotFoundException(MessageError.PRODUCT_VARIANT_NOT_FOUND);
        }
    }

    public Page<ProductView> getProductView(Long ownerId, Long shopId, Long categoryId, ProductStatus status, ShopStatus shopStatus, String keyword, Integer star, Double startPrice, Double endPrice, Pageable pageable) {
        List<Criteria> criteriaList = new ArrayList<>();
        if (FnCommon.isNotNull(ownerId)) {
            criteriaList.add(Criteria.where("ownerId").is(String.valueOf(ownerId)));
        }
        if (FnCommon.isNotNull(shopId)) {
            criteriaList.add(Criteria.where("shopId").is(String.valueOf(shopId)));
        }
        if (FnCommon.isNotNull(shopStatus)) {
            criteriaList.add(Criteria.where("shopStatus").is(shopStatus));
        }
        if (FnCommon.isNotNull(categoryId)) {
            criteriaList.add(Criteria.where("categoryId").is(String.valueOf(categoryId)));
        }
        if (FnCommon.isNotNull(status)) {
            criteriaList.add(Criteria.where("productStatus").is(status));
        }
        if (FnCommon.isNotNullOrEmpty(keyword)) {
            criteriaList.add(Criteria.where("name").regex(keyword, "i"));
        }

        if (FnCommon.isNotNull(star)) {
            criteriaList.add(Criteria.where("rating").gte(star.doubleValue()));
        }

        if (FnCommon.isNotNull(startPrice) || FnCommon.isNotNull(endPrice)) {
            if (FnCommon.isNotNull(startPrice) && FnCommon.isNotNull(endPrice)) {
                criteriaList.add(Criteria.where("productVariants.price").gte(startPrice).lte(endPrice));
            } else if (FnCommon.isNotNull(startPrice)) {
                criteriaList.add(Criteria.where("productVariants.price").gte(startPrice));
            } else {
                criteriaList.add(Criteria.where("productVariants.price").lte(endPrice));
            }
        }
        Criteria finalCriteria = new Criteria();
        if (!criteriaList.isEmpty()) {
            finalCriteria = finalCriteria.andOperator(criteriaList.toArray(new Criteria[0]));
        }
        Query query = new Query(finalCriteria);
        long total = mongoTemplate.count(query, ProductView.class);
        query.with(pageable);
        List<ProductView> productViews = mongoTemplate.find(query, ProductView.class);
        return new PageImpl<>(productViews, pageable, total);
    }

    public void updateRating(Long productId, RatingNumber rating, Boolean isUpdate, RatingNumber oldRating, Boolean isDelete) {
        Query query = new Query(Criteria.where("_id").is(String.valueOf(productId)));
        Update update;
        if (Boolean.TRUE.equals(isDelete)) {
            update = new Update()
                .inc("numberOfReviews", -1);
            if (FnCommon.isNotNull(oldRating)) {
                update.inc("numberOfRatings", -1)
                    .inc("rating", -oldRating.getValue())
                    .inc("ratingStatistics." + oldRating.name(), -1);
            }
        } else if (Boolean.TRUE.equals(isUpdate)) {
            Integer updateValue;
            if (FnCommon.isNotNull(oldRating) && FnCommon.isNotNull(rating)) {
                updateValue = rating.getValue() - oldRating.getValue();
                update = new Update()
                    .inc("rating", updateValue)
                    .inc("ratingStatistics." + oldRating.name(), -1)
                    .inc("ratingStatistics." + rating.name(), 1);
            } else if (FnCommon.isNotNull(oldRating)) {
                updateValue = -oldRating.getValue();
                update = new Update()
                    .inc("rating", updateValue)
                    .inc("ratingStatistics." + oldRating.name(), -1);
            } else if (FnCommon.isNotNull(rating)) {
                updateValue = rating.getValue();
                update = new Update()
                    .inc("rating", updateValue)
                    .inc("ratingStatistics." + rating.name(), 1);
            } else {
                updateValue = 0;
                update = new Update()
                    .inc("rating", updateValue);
            }
        } else {
            update = new Update()
                .inc("numberOfReviews", 1);
            if (FnCommon.isNotNull(rating)) {
                update.inc("numberOfRatings", 1)
                    .inc("rating", rating.getValue())
                    .inc("ratingStatistics." + rating.name(), 1);
            }
        }
        mongoTemplate.updateFirst(query, update, ProductView.class);
    }

    /**
     * Thống kê sản phẩm bán chạy hoặc doanh thu cao trong tháng
     *
     * @param shopId        ID của shop (optional)
     * @param isOwner       Xác định người dùng hiện tại có phải là chủ sở hữu không
     * @param currentUserId ID của người dùng hiện tại
     * @param nowDate       Thời điểm hiện tại để xác định tháng
     * @param type          Loại thống kê: "sold" (bán chạy) hoặc "revenue" (doanh thu cao)
     * @return Danh sách thống kê sản phẩm
     */
    public List<ProductViewStatisticDTO> getProductStatistics(
        String shopId, Boolean isOwner, Long currentUserId, LocalDateTime nowDate, String type) {

        List<Criteria> criteriaList = new ArrayList<>();

        // Điều kiện lọc dựa trên type
        if ("revenue".equalsIgnoreCase(type)) {
            criteriaList.add(Criteria.where("totalRevenue").gt(BigDecimal.ZERO));
        } else {
            criteriaList.add(Criteria.where("totalSold").gt(0));
        }
        if (FnCommon.isNotNull(nowDate)) {
            LocalDateTime startOfMonth = nowDate.toLocalDate().withDayOfMonth(1).atStartOfDay();
            LocalDateTime endOfMonth = startOfMonth.plusMonths(1);
            criteriaList.add(Criteria.where("createdAt").gte(startOfMonth).lt(endOfMonth));
        }

        if (FnCommon.isNotNullOrEmpty(shopId)) {
            criteriaList.add(Criteria.where("shopId").is(shopId));
        } else if (Boolean.TRUE.equals(isOwner)) {
            criteriaList.add(Criteria.where("ownerId").is(String.valueOf(currentUserId)));
        }

        Criteria finalCriteria = new Criteria().andOperator(criteriaList.toArray(new Criteria[0]));


        MatchOperation match = Aggregation.match(finalCriteria);

        GroupOperation group = Aggregation.group("_id")
            .first("name").as("productName")
            .first("totalSold").as("totalSold")
            .first("totalRevenue").as("totalRevenue");

        // Sắp xếp theo type
        String sortField = "revenue".equalsIgnoreCase(type) ? "totalRevenue" : "totalSold";
        SortOperation sort = Aggregation.sort(Sort.Direction.DESC, sortField);

        LimitOperation limit = Aggregation.limit(5);
        Aggregation aggregation = Aggregation.newAggregation(match, group, sort, limit);
        AggregationResults<Document> results = mongoTemplate.aggregate(aggregation, "product_views", Document.class);
        List<ProductViewStatisticDTO> statistics = new ArrayList<>();
        for (Document doc : results.getMappedResults()) {
            statistics.add(ProductViewStatisticDTO.builder()
                .productId(doc.getString("_id"))
                .productName(doc.getString("productName"))
                .totalSold(doc.getInteger("totalSold", 0))
                .totalRevenue(doc.get("totalRevenue") != null ? new BigDecimal(doc.get("totalRevenue").toString()) : BigDecimal.ZERO)
                .build());
        }
        return statistics;
    }
}
