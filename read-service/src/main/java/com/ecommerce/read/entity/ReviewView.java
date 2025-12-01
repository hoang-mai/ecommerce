package com.ecommerce.read.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Document(collection = "review_views")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewView extends BaseEntity {
    @Id
    @Field("_id")
    @JsonProperty("reviewId")
    private String _id;

    @Field("orderItemId")
    private String orderItemId;

    @Field("productId")
    private String productId;

    @Field("productVariantId")
    private String productVariantId;

    @Field("userId")
    private String userId;

    @Field("rating")
    private Double rating;

    @Field("comment")
    private String comment;

    @Field("imageUrls")
    private List<String> imageUrls;

    @Field("attributes")
    private Map<String, String> attributes;

    @Field("reviewReplyView")
    private ReviewReplyView reviewReplyView;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReviewReplyView extends  BaseEntity {
        @Id
        @JsonProperty("replyId")
        private String replyId;

        @Field("replierId")
        private String replierId;

        @Field("content")
        private String content;
    }
}
