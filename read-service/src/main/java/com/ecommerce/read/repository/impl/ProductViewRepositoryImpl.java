package com.ecommerce.read.repository.impl;

import com.ecommerce.library.enumeration.ProductStatus;
import com.ecommerce.library.enumeration.ShopStatus;
import com.ecommerce.library.exception.NotFoundException;
import com.ecommerce.library.kafka.event.product.UpdateProductVariantStatusEvent;
import com.ecommerce.library.utils.FnCommon;
import com.ecommerce.library.utils.MessageError;
import com.ecommerce.read.entity.ProductView;
import com.ecommerce.read.entity.UserView;
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

    public Page<ProductView> getProductView(Long ownerId, Long shopId, Long categoryId, ProductStatus status, ShopStatus shopStatus, String keyword, Pageable pageable) {
        List<Criteria> criteriaList = new ArrayList<>();
        if (FnCommon.isNotNull(ownerId)) {
            criteriaList.add(Criteria.where("ownerId").is(String.valueOf(ownerId)));
        }
        if (FnCommon.isNotNull(shopId)) {
            criteriaList.add(Criteria.where("shopId").is(String.valueOf(shopId)));
        }
        if( FnCommon.isNotNull(shopStatus)) {
            criteriaList.add(Criteria.where("shopStatus").is(shopStatus));
        }
        if (FnCommon.isNotNull(categoryId)) {
            criteriaList.add(Criteria.where("categoryId").is(String.valueOf(categoryId)));
        }
        if (FnCommon.isNotNull(status)) {
            criteriaList.add(Criteria.where("productStatus").is(status));
        }
        if (FnCommon.isNotNullOrEmpty(keyword)) {
            criteriaList.add(Criteria.where("productName").regex(keyword, "i"));
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
}
