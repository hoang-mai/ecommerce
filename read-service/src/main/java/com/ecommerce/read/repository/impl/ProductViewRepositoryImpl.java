package com.ecommerce.read.repository.impl;

import com.ecommerce.library.exception.NotFoundException;
import com.ecommerce.library.kafka.event.product.UpdateProductVariantStatusEvent;
import com.ecommerce.library.utils.MessageError;
import com.ecommerce.read.entity.ProductView;
import com.mongodb.client.result.UpdateResult;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ProductViewRepositoryImpl {

    private final MongoTemplate mongoTemplate;

    public void updateProductVariantStatus(UpdateProductVariantStatusEvent event) {
        Query query = new Query(Criteria.where("productId")
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
}
