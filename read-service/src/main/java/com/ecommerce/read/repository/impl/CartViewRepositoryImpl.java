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
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

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
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("userId").is(currentUserId)),
                Aggregation.unwind("cartItems"),
                Aggregation.lookup("shop_views", "cartItems.shopId", "_id", "shop_views"),
                Aggregation.unwind("shop_views"),
                Aggregation.unwind("cartItems.productCartItems"),
                Aggregation.lookup("product_views", "cartItems.productCartItems.productId", "_id", "product_views"),
                Aggregation.unwind("product_views"),
                Aggregation.project()
                        .and("_id").as("cartId")
                        .and("cartItems._id").as("cartItemId")
                        .and("shop_views").as("shopView")
                        .and("cartItems.productCartItems._id").as("productCartItemId")
                        .and("product_views").as("productView")
                        .and("cartItems.productCartItems.productVariantId").as("productVariantId")
                        .and("cartItems.productCartItems.quantity").as("quantity"),
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
