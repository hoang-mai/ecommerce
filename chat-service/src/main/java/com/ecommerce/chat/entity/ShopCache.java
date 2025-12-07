package com.ecommerce.chat.entity;

import com.ecommerce.library.enumeration.ShopStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Document(collection = "shop_caches")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class ShopCache extends BaseEntity {

    @Id
    @JsonProperty("shopId")
    private String _id;

    @Field("shopName")
    private String shopName;

    @Field("logoUrl")
    private String logoUrl;

    @Field("ownerId")
    private String ownerId;

    @Field("is_online")
    private Boolean isOnline;

    @Field(name = "shopStatus")
    private ShopStatus shopStatus;

}
