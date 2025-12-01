package com.ecommerce.read.repository.impl;

import com.ecommerce.library.enumeration.ProductStatus;
import com.ecommerce.library.enumeration.RatingNumber;
import com.ecommerce.library.enumeration.ShopStatus;
import com.ecommerce.library.exception.NotFoundException;
import com.ecommerce.library.kafka.event.product.UpdateProductVariantStatusEvent;
import com.ecommerce.library.utils.FnCommon;
import com.ecommerce.library.utils.MessageError;
import com.ecommerce.read.entity.ProductView;
import com.mongodb.client.result.UpdateResult;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

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
            if(FnCommon.isNotNull(oldRating)){
                update.inc("numberOfRatings", -1)
                        .inc("rating", -oldRating.getValue())
                        .inc("ratingStatistics." + oldRating.name(), -1);
            }
        } else if (Boolean.TRUE.equals(isUpdate)) {
            Integer updateValue;
            if(FnCommon.isNotNull(oldRating) && FnCommon.isNotNull(rating)){
                updateValue = rating.getValue() - oldRating.getValue();
                update = new Update()
                    .inc("rating", updateValue)
                    .inc("ratingStatistics." + oldRating.name(), -1)
                    .inc("ratingStatistics." + rating.name(), 1);
            } else if(FnCommon.isNotNull(oldRating)){
                updateValue = - oldRating.getValue();
                update = new Update()
                    .inc("rating", updateValue)
                    .inc("ratingStatistics." + oldRating.name(), -1);
            } else if(FnCommon.isNotNull(rating)){
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
            if(FnCommon.isNotNull(rating)){
                update.inc("numberOfRatings", 1)
                      .inc("rating", rating.getValue())
                      .inc("ratingStatistics." + rating.name(), 1);
            }
        }
        mongoTemplate.updateFirst(query, update, ProductView.class);
    }
}
