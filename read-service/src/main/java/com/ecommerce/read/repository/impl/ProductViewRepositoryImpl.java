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
import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

    public Page<ProductView> getProductView(List<String> productIds, Long ownerId, Long shopId, Long categoryId, ProductStatus status, ShopStatus shopStatus, String keyword, Integer star, BigDecimal startPrice, BigDecimal endPrice, Pageable pageable) {
        List<Criteria> criteriaList = new ArrayList<>();
        if (FnCommon.isNotNullOrEmptyList(productIds)) {
            criteriaList.add(Criteria.where("_id").in(productIds));
        }
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
                criteriaList.add(Criteria.where("$expr").is(
                    new Document("$and", List.of(
                        new Document("$gte", List.of(new Document("$toDouble", "$basePrice"), startPrice.doubleValue())),
                        new Document("$lte", List.of(new Document("$toDouble", "$basePrice"), endPrice.doubleValue()))
                    ))
                ));
            } else if (FnCommon.isNotNull(startPrice)) {
                criteriaList.add(Criteria.where("$expr").is(
                    new Document("$gte", List.of(new Document("$toDouble", "$basePrice"), startPrice.doubleValue()))
                ));
            } else {
                criteriaList.add(Criteria.where("$expr").is(
                    new Document("$lte", List.of(new Document("$toDouble", "$basePrice"), endPrice.doubleValue()))
                ));
            }
        }

        Instant now = Instant.now();

        // Build match criteria
        Criteria matchCriteria = new Criteria();
        if (!criteriaList.isEmpty()) {
            matchCriteria = matchCriteria.andOperator(criteriaList.toArray(new Criteria[0]));
        }

        // Count aggregation for total
        Aggregation countAggregation = Aggregation.newAggregation(
            Aggregation.match(matchCriteria),
            Aggregation.count().as("total")
        );
        AggregationResults<Document> countResult = mongoTemplate.aggregate(countAggregation, "product_views", Document.class);
        long total = countResult.getMappedResults().isEmpty() ? 0 : countResult.getMappedResults().get(0).getInteger("total", 0);

        // Main aggregation with lookup for flash sale products
        List<AggregationOperation> operations = new ArrayList<>();
        operations.add(Aggregation.match(matchCriteria));

        // Lookup flash sale products that are currently active
        operations.add(Aggregation.lookup()
            .from("flash_sale_product_views")
            .localField("_id")
            .foreignField("productId")
            .as("flashSaleProductViews"));

        // Filter flash sale products to only include active ones
        operations.add(Aggregation.addFields()
            .addField("flashSaleProductViews")
            .withValue(new Document("$filter", new Document()
                .append("input", "$flashSaleProductViews")
                .append("as", "fsp")
                .append("cond", new Document("$and", List.of(
                    new Document("$lte", List.of("$$fsp.startTime", now)),
                    new Document("$gte", List.of("$$fsp.endTime", now)),
                    new Document("$ne", List.of("$$fsp.isSoldOut", true))
                )))))
            .build());

        // Sort and pagination
        if (pageable.getSort().isSorted()) {
            operations.add(Aggregation.sort(pageable.getSort()));
        }
        operations.add(Aggregation.skip((long) pageable.getPageNumber() * pageable.getPageSize()));
        operations.add(Aggregation.limit(pageable.getPageSize()));

        Aggregation aggregation = Aggregation.newAggregation(operations);
        AggregationResults<ProductView> results = mongoTemplate.aggregate(aggregation, "product_views", ProductView.class);
        List<ProductView> productViews = results.getMappedResults();

        return new PageImpl<>(productViews, pageable, total);
    }

    public List<ProductView> getProductViewByIdsPreserveOrder(List<String> productIds) {
        if (!FnCommon.isNotNullOrEmptyList(productIds)) {
            return List.of();
        }
        Query query = new Query(Criteria.where("_id").in(productIds));
        List<ProductView> productViews = mongoTemplate.find(query, ProductView.class);

        return productIds.stream()
            .map(id -> productViews.stream()
                .filter(pv -> pv.get_id().equals(id))
                .findFirst()
                .orElse(null))
            .filter(Objects::nonNull)
            .toList();
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
        String shopId, Boolean isOwner, Long currentUserId, Instant nowDate, String type) {

        List<Criteria> criteriaList = new ArrayList<>();

        // Điều kiện lọc dựa trên type
        if ("revenue".equalsIgnoreCase(type)) {
            criteriaList.add(Criteria.where("totalRevenue").gt(BigDecimal.ZERO));
        } else {
            criteriaList.add(Criteria.where("totalSold").gt(0));
        }
        if (FnCommon.isNotNull(nowDate)) {
            ZoneId zoneId = ZoneId.of("Asia/Ho_Chi_Minh");

            Instant startOfMonth = nowDate
                .atZone(zoneId)
                .toLocalDate()
                .withDayOfMonth(1)
                .atStartOfDay(zoneId)
                .toInstant();

            Instant endOfMonth = startOfMonth
                .atZone(zoneId)
                .plusMonths(1)
                .toInstant();
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

        // Project để convert totalRevenue từ string sang double cho việc sort
        ProjectionOperation projectForSort = Aggregation.project()
            .andInclude("productName", "totalRevenue", "totalSold")
            .and(ConvertOperators.ToDouble.toDouble("$totalRevenue")).as("totalRevenueDouble");

        // Sắp xếp theo type
        String sortField = "revenue".equalsIgnoreCase(type) ? "totalRevenueDouble" : "totalSold";
        SortOperation sort = Aggregation.sort(Sort.Direction.DESC, sortField);

        LimitOperation limit = Aggregation.limit(5);
        Aggregation aggregation = Aggregation.newAggregation(match, group, projectForSort, sort, limit);
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

    public List<ProductView> getHomepageProducts(List<String> showProductIds, String categoryId, Sort sort, int limit) {
        Instant now = Instant.now();

        List<Criteria> criteriaList = new ArrayList<>();
        if (FnCommon.isNotNullOrEmptyList(showProductIds)) {
            criteriaList.add(Criteria.where("_id").nin(showProductIds));
        }
        if (FnCommon.isNotNullOrEmpty(categoryId)) {
            criteriaList.add(Criteria.where("categoryId").is(categoryId));
        }
        criteriaList.add(Criteria.where("productStatus").is(ProductStatus.ACTIVE));
        criteriaList.add(Criteria.where("shopStatus").is(ShopStatus.ACTIVE));

        Criteria matchCriteria = new Criteria().andOperator(criteriaList.toArray(new Criteria[0]));

        List<AggregationOperation> operations = new ArrayList<>();
        operations.add(Aggregation.match(matchCriteria));

        // Lookup flash sale products
        operations.add(Aggregation.lookup()
            .from("flash_sale_product_views")
            .localField("_id")
            .foreignField("productId")
            .as("flashSaleProductViews"));

        // Filter flash sale products to only include active ones
        operations.add(Aggregation.addFields()
            .addField("flashSaleProductViews")
            .withValue(new Document("$filter", new Document()
                .append("input", "$flashSaleProductViews")
                .append("as", "fsp")
                .append("cond", new Document("$and", List.of(
                    new Document("$lte", List.of("$$fsp.startTime", now)),
                    new Document("$gte", List.of("$$fsp.endTime", now)),
                    new Document("$ne", List.of("$$fsp.isSoldOut", true))
                )))))
            .build());

        // Sort
        operations.add(Aggregation.sort(sort));

        // Limit
        operations.add(Aggregation.limit(limit));

        Aggregation aggregation = Aggregation.newAggregation(operations);
        AggregationResults<ProductView> results = mongoTemplate.aggregate(aggregation, "product_views", ProductView.class);
        return results.getMappedResults();
    }

    public Long countProductsForHomepage() {
        Query query = new Query();
        query.addCriteria(Criteria.where("productStatus").is(ProductStatus.ACTIVE));
        query.addCriteria(Criteria.where("shopStatus").is(ShopStatus.ACTIVE));
        return mongoTemplate.count(query, ProductView.class);
    }
}
