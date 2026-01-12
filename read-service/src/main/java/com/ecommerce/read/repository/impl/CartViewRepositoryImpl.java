package com.ecommerce.read.repository.impl;

import com.ecommerce.library.exception.NotFoundException;
import com.ecommerce.library.kafka.event.cart.UpdateProductCartItemEvent;
import com.ecommerce.library.utils.MessageError;
import com.ecommerce.read.dto.CartViewDTO;
import com.ecommerce.read.entity.CartView;
import com.mongodb.client.result.UpdateResult;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.Arrays;

@Repository
@RequiredArgsConstructor
public class CartViewRepositoryImpl {

    private final MongoTemplate mongoTemplate;

    public void updateCartItem(UpdateProductCartItemEvent event) {
        Query query = new Query(Criteria.where("_id")
                .is(String.valueOf(event.getCartId())));

        Update update = new Update();
        if (event.getQuantity() != null) {
            update.set("cartItems.$[item].productCartItems.$[product].quantity",
                    event.getQuantity());
        }

        update.filterArray(Criteria.where("item._id")
                .is(String.valueOf(event.getCartItemId())));
        update.filterArray(Criteria.where("product._id")
                .is(String.valueOf(event.getProductCartItemId())));

        UpdateResult result = mongoTemplate.updateFirst(query, update, CartView.class);

        if (result.getMatchedCount() == 0) {
            throw new NotFoundException(MessageError.CART_NOT_FOUND);
        }
        if (result.getModifiedCount() == 0) {
            throw new NotFoundException(MessageError.CART_ITEM_NOT_FOUND);
        }
    }

    public CartViewDTO findCartViewDTOByUserId(String currentUserId) {
        AggregationOperation flashSaleLookup = context -> new Document("$lookup",
                new Document("from", "flash_sale_product_views")
                        .append("let", new Document("productId", "$cartItems.productCartItems.productId")
                                .append("variantId", "$cartItems.productCartItems.productVariantId"))
                        .append("pipeline", Arrays.asList(
                                new Document("$match",
                                        new Document("$expr",
                                                new Document("$and", Arrays.asList(
                                                        new Document("$eq", Arrays.asList("$productId", "$$productId")),
                                                        new Document("$eq", Arrays.asList("$productVariantId", "$$variantId")),
                                                        new Document("$lte", Arrays.asList("$startTime", "$$NOW")),
                                                        new Document("$gte", Arrays.asList("$endTime", "$$NOW"))
                                                ))
                                        )
                                ),
                                new Document("$limit", 1)
                        ))
                        .append("as", "flash_sale_views")
        );

        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("userId").is(currentUserId)),
                Aggregation.unwind("cartItems"),
                Aggregation.lookup("shop_views", "cartItems.shopId", "_id", "shop_views"),
                Aggregation.unwind("shop_views"),
                Aggregation.unwind("cartItems.productCartItems"),
                Aggregation.lookup("product_views", "cartItems.productCartItems.productId", "_id", "product_views"),
                Aggregation.unwind("product_views"),
                flashSaleLookup,
                Aggregation.project()
                        .and("_id").as("cartId")
                        .and("cartItems._id").as("cartItemId")
                        .and("shop_views").as("shopView")
                        .and("cartItems.productCartItems._id").as("productCartItemId")
                        .and("product_views").as("productView")
                        .and("cartItems.productCartItems.productVariantId").as("productVariantId")
                        .and("cartItems.productCartItems.quantity").as("quantity")
                        .and("flash_sale_views").as("flashSaleProductView"),
                Aggregation.group("cartId", "cartItemId")
                        .first("cartId").as("cartId")
                        .first("cartItemId").as("cartItemId")
                        .first("shopView").as("shopView")
                        .push(
                                new Document()
                                        .append("_id", "$productCartItemId")
                                        .append("productView", "$productView")
                                        .append("productVariantId", "$productVariantId")
                                        .append("quantity", "$quantity")
                                        .append("flashSaleProductView", "$flashSaleProductView")
                        )
                        .as("productCartItems"),
                Aggregation.group("cartId")
                        .push(
                                new Document()
                                        .append("_id", "$cartItemId")
                                        .append("shopView", "$shopView")
                                        .append("productCartItems", "$productCartItems")
                        ).as("cartItems")
        );
        AggregationResults<CartViewDTO> results = mongoTemplate.aggregate(
                aggregation, "cart_views", CartViewDTO.class
        );

        return results.getUniqueMappedResult();
    }
}
