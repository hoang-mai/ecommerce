package com.ecommerce.product.entity;

import com.ecommerce.library.entity.BaseEntity;
import com.ecommerce.library.enumeration.ShopStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "shops")
public class Shop extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "shop_id", unique = true)
    private Long shopId;

    @Column(name = "owner_id")
    private Long ownerId;

    @Column(name = "shop_name")
    private String shopName;

    @Column(name = "description")
    private String description;

    @Column(name = "logo_url")
    private String logoUrl;

    @Column(name = "banner_url")
    private String bannerUrl;

    @Column(name = "rating")
    private Double rating;

    @Enumerated(EnumType.STRING)
    @Column(name = "shop_status")
    private ShopStatus shopStatus;

    @Column(name = "province")
    private String province;

    @Column(name = "ward")
    private String ward;

    @Column(name = "detail")
    private String detail;

    @Column(name = "phone_number")
    private String phoneNumber;

}
