package com.ecommerce.read.repository.impl;

import com.ecommerce.library.enumeration.ProductStatus;
import com.ecommerce.library.enumeration.RatingNumber;
import com.ecommerce.library.enumeration.ShopStatus;
import com.ecommerce.library.utils.FnCommon;
import com.ecommerce.read.dto.NewShopViewStatisticDTO;
import com.ecommerce.read.dto.OrderViewStatisticDTO;
import com.ecommerce.read.dto.OwnerViewStatisticDTO;
import com.ecommerce.read.dto.ShopViewStatisticDTO;
import com.ecommerce.read.entity.ShopView;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
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

    public void updateRating(String shopId, RatingNumber rating, Boolean isUpdate, RatingNumber oldRating, Boolean isDelete) {
        Query query = new Query(Criteria.where("_id").is(shopId));
        Update update;
        if (Boolean.TRUE.equals(isDelete)) {
            update = new Update()
                .inc("numberOfReviews", -1);
            if (FnCommon.isNotNull(oldRating)) {
                update.inc("numberOfRatings", -1)
                    .inc("rating", -oldRating.getValue());
            }
        } else if (Boolean.TRUE.equals(isUpdate)) {
            Integer updateValue;
            if (FnCommon.isNotNull(oldRating) && FnCommon.isNotNull(rating)) {
                updateValue = rating.getValue() - oldRating.getValue();
            } else if (FnCommon.isNotNull(oldRating)) {
                updateValue = -oldRating.getValue();
            } else if (FnCommon.isNotNull(rating)) {
                updateValue = rating.getValue();
            } else {
                updateValue = 0;
            }
            update = new Update()
                .inc("rating", updateValue);
        } else {
            update = new Update()
                .inc("numberOfReviews", 1);
            if (FnCommon.isNotNull(rating)) {
                update.inc("numberOfRatings", 1)
                    .inc("rating", rating.getValue());
            }
        }
        mongoTemplate.updateFirst(query, update, ShopView.class);
    }

    public void incrementProductCount(Long shopId) {
        Query query = new Query(Criteria.where("_id").is(String.valueOf(shopId)));
        Update update = new Update().inc("totalProducts", 1).inc("activeProducts", 1);
        mongoTemplate.updateFirst(query, update, ShopView.class);
    }

    public void updateProductStatusInShopView(String shopId, ProductStatus status) {
        Query query = new Query(Criteria.where("_id").is(shopId));
        Update update = new Update();
        if (status == ProductStatus.ACTIVE) {
            update.inc("activeProducts", 1);
        } else if (status == ProductStatus.INACTIVE) {
            update.inc("activeProducts", -1);
        }
        mongoTemplate.updateFirst(query, update, ShopView.class);
    }

    public void incrementTotalSoldAndTotalOrder(String shopId, int quantity) {
        Query query = new Query(Criteria.where("_id").is(shopId));
        Update update = new Update().inc("totalSold", quantity).inc("totalOrder", 1);
        mongoTemplate.updateFirst(query, update, ShopView.class);
    }

    public OwnerViewStatisticDTO getOverviewStatistics(String ownerId) {
        Criteria criteria = Criteria.where("ownerId").is(ownerId);
        MatchOperation match = Aggregation.match(criteria);
        ProjectionOperation project = Aggregation.project()
            .andInclude("totalSold", "totalProducts", "totalOrder")
            .and(ConvertOperators.ToDouble.toDouble("$totalRevenue")).as("totalRevenueDouble");

        GroupOperation group = Aggregation.group()
            .sum("totalSold").as("totalSold")
            .sum("totalProducts").as("totalProducts")
            .sum("totalOrder").as("totalOrders")
            .sum("totalRevenueDouble").as("totalRevenueDouble");

        Aggregation aggregation = Aggregation.newAggregation(match,project, group);
        AggregationResults<Document> results = mongoTemplate.aggregate(aggregation, "shop_views", Document.class);
        Document doc = results.getUniqueMappedResult();

        BigDecimal totalRevenue = BigDecimal.ZERO;
        int totalProducts = 0;
        int totalOrders = 0;
        int totalSold = 0;

        if (doc != null) {
            Object rev = doc.get("totalRevenueDouble");
            if (rev != null) {
                try {
                    totalRevenue = new BigDecimal(rev.toString());
                } catch (Exception ignored) {
                    totalRevenue = BigDecimal.ZERO;
                }
            }
            Object prod = doc.get("totalProducts");
            if (prod != null) {
                try {
                    totalProducts = ((Number) prod).intValue();
                } catch (Exception ignored) {
                    totalProducts = 0;
                }
            }
            Object ord = doc.get("totalOrders");
            if (ord != null) {
                try {
                    totalOrders = ((Number) ord).intValue();
                } catch (Exception ignored) {
                    totalOrders = 0;
                }
            }
            Object shops = doc.get("totalSold");
            if (shops != null) {
                try {
                    totalSold = ((Number) shops).intValue();
                } catch (Exception ignored) {
                    totalSold = 0;
                }
            }
        }

        return OwnerViewStatisticDTO.builder()
            .totalRevenue(totalRevenue)
            .totalProducts(totalProducts)
            .totalOrders(totalOrders)
            .totalSold(totalSold)
            .build();
    }

    /**
     * Lấy top 5 shop theo doanh thu hoặc số lượng bán trong tháng hiện tại
     * @param ownerId ID chủ shop
     * @param nowDate Thời điểm hiện tại để xác định tháng (optional, mặc định là thời điểm hiện tại)
     * @param type    Loại thống kê: "sold" (bán chạy) hoặc "revenue" (doanh thu cao)
     * @return Danh sách top 5 shop
     */
    public List<ShopViewStatisticDTO> getTopShopsByRevenue(Long ownerId,LocalDateTime nowDate, String type) {
        List<Criteria> criteriaList = new ArrayList<>();

        if(FnCommon.isNotNull(ownerId)) {
            criteriaList.add(Criteria.where("ownerId").is(String.valueOf(ownerId)));
        }

        // Điều kiện lọc dựa trên type
        if ("revenue".equalsIgnoreCase(type)) {
            criteriaList.add(Criteria.where("totalRevenue").gt(BigDecimal.ZERO));
        } else {
            criteriaList.add(Criteria.where("totalSold").gt(0L));
        }

        // Filter theo tháng nếu có nowDate
        if (FnCommon.isNotNull(nowDate)) {
            LocalDateTime startOfMonth = nowDate.toLocalDate().withDayOfMonth(1).atStartOfDay();
            LocalDateTime endOfMonth = startOfMonth.plusMonths(1);
            criteriaList.add(Criteria.where("createdAt").gte(startOfMonth).lt(endOfMonth));
        }

        Criteria finalCriteria = new Criteria().andOperator(criteriaList.toArray(new Criteria[0]));

        MatchOperation match = Aggregation.match(finalCriteria);

        // Group by shop _id và lấy các thông tin cần thiết
        GroupOperation group = Aggregation.group("_id")
            .first("shopName").as("shopName")
            .first("totalRevenue").as("totalRevenue")
            .first("totalSold").as("totalSold")
            .first("totalOrder").as("totalOrder");

        // Project để convert totalRevenue từ string sang double cho việc sort
        ProjectionOperation projectForSort = Aggregation.project()
            .andInclude("shopName", "totalRevenue", "totalSold", "totalOrder")
            .and(ConvertOperators.ToDouble.toDouble("$totalRevenue")).as("totalRevenueDouble");

        // Sắp xếp theo type
        String sortField = "revenue".equalsIgnoreCase(type) ? "totalRevenueDouble" : "totalSold";
        SortOperation sort = Aggregation.sort(Sort.Direction.DESC, sortField);

        // Giới hạn 5 kết quả
        LimitOperation limit = Aggregation.limit(5);

        Aggregation aggregation = Aggregation.newAggregation(match, group, projectForSort, sort, limit);
        AggregationResults<Document> results = mongoTemplate.aggregate(aggregation, "shop_views", Document.class);

        List<ShopViewStatisticDTO> statistics = new ArrayList<>();
        for (Document doc : results.getMappedResults()) {
            statistics.add(ShopViewStatisticDTO.builder()
                .shopId(doc.getString("_id"))
                .shopName(doc.getString("shopName"))
                .totalRevenue(doc.get("totalRevenue") != null ? new BigDecimal(doc.get("totalRevenue").toString()) : BigDecimal.ZERO)
                .totalSold(doc.get("totalSold") != null ? ((Number) doc.get("totalSold")).longValue() : 0L)
                .totalOrder(doc.get("totalOrder") != null ? ((Number) doc.get("totalOrder")).longValue() : 0L)
                .build());
        }
        return statistics;
    }

    public List<NewShopViewStatisticDTO> getStatisticsByDateRange(LocalDateTime fromDate, LocalDateTime toDate) {
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
            .count().as("newShopViews");


        SortOperation sort =
            Aggregation.sort(Sort.Direction.ASC, "_id");

        Aggregation agg = Aggregation.newAggregation(match, project, group, sort);

        AggregationResults<Document> results = mongoTemplate.aggregate(agg, "shop_views", Document.class);

        List<NewShopViewStatisticDTO> statistics = new ArrayList<>();

        results.getMappedResults().forEach(doc -> {
            Object id = doc.get("_id");
            Object count = doc.get("newShopViews");
            if (id != null && count != null) {
                try {
                    String monthString = id.toString();
                    Integer newShopViews = ((Number) count).intValue();
                    statistics.add(NewShopViewStatisticDTO.builder()
                        .localDate(monthString)
                        .newShopViews(newShopViews)
                        .build());
                } catch (Exception ignored) {
                }
            }
        });

        return statistics;
    }
}

