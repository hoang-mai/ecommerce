package com.ecommerce.read.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "interactions")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Interaction extends BaseEntity {

    @Id
    @JsonProperty("interactionId")
    private String _id;

    @Field("userId")
    private String userId;

    @Field("productId")
    private String productId;

    @Field("interactionType")
    private InteractionType interactionType;

    @Field("interactionCount")
    @Builder.Default
    private Long interactionCount = 1L;

    @Field("metadata")
    private InteractionMetadata metadata;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InteractionMetadata {
        private String sessionId;
        private String ipAddress;
        private String userAgent;
        private String referrer;
        private Long duration;
        private Integer quantity;
    }

    public enum InteractionType {
        VIEW,           // Xem sản phẩm
        CLICK,          // Click vào sản phẩm
        ADD_TO_CART,    // Thêm vào giỏ hàng
        REMOVE_FROM_CART, // Xóa khỏi giỏ hàng
        PURCHASE,       // Mua hàng
        FAVORITE,       // Yêu thích
        UNFAVORITE,     // Bỏ yêu thích
        SHARE,          // Chia sẻ
        SEARCH          // Tìm kiếm sản phẩm
    }

    public void incrementInteractionCount() {
        this.interactionCount++;
    }
}
