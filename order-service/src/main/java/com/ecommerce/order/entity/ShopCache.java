package com.ecommerce.order.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "shop_caches")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShopCache {
    @Id
    @Column(name = "shop_id")
    private Long shopId;

}
