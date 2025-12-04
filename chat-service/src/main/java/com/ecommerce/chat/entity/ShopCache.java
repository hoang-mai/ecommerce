package com.ecommerce.chat.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Document(collection = "shop_cache")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShopCache extends BaseEntity {

    @Id
    @Field("_id")
    private String _id;

    @Field("shop_id")
    private String shopId;

    @Field("shop_name")
    private String shopName;

    @Field("shop_avatar_url")
    private String shopAvatarUrl;

    @Field("shop_description")
    private String shopDescription;

    @Field("owner_id")
    private String ownerId;

    @Field("is_online")
    private Boolean isOnline;

    @Field("is_active")
    private Boolean isActive;

    @Field("response_rate")
    private Double responseRate; // Percentage of messages responded to

    @Field("average_response_time")
    private Long averageResponseTime; // In minutes

    @Field("cache_expires_at")
    private LocalDateTime cacheExpiresAt; // TTL for cache invalidation
}
