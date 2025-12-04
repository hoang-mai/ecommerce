package com.ecommerce.review.entity;


import com.ecommerce.library.entity.BaseEntity;
import com.ecommerce.library.enumeration.RatingNumber;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "reviews")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Review extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "review_id", updatable = false, nullable = false)
    private Long reviewId;

    @Enumerated(EnumType.STRING)
    @Column(name = "rating_number", nullable = false)
    private RatingNumber ratingNumber;

    @Column(name = "comment", columnDefinition = "TEXT")
    private String comment;

    @Column(name = "order_item_id", nullable = false)
    private Long orderItemId;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "shop_id")
    private Long shopId;

    @Column(name = "owner_id")
    private Long ownerId;

    @Column(name = "product_variant_id", nullable = false)
    private Long productVariantId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "isUpdated", nullable = false)
    @Builder.Default
    private Boolean isUpdated = false;

    @CollectionTable(name = "review_images", joinColumns = @JoinColumn(name = "review_id"))
    @ElementCollection
    @Column(name = "image_url")
    @OrderColumn(name = "image_order")
    @Builder.Default
    private List<String> imageUrls = new ArrayList<>();

    @CollectionTable(name = "product_attribute", joinColumns = @JoinColumn(name = "review_id"))
    @ElementCollection
    @MapKeyJoinColumn(name = "attribute_key")
    @Column(name = "attribute_value")
    @Builder.Default
    private Map<String, String> attributes = new HashMap<>();


    @OneToOne(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
    private ReviewReply reviewReply;

    public void deleteImageUrl(String imageUrl) {
        this.imageUrls.remove(imageUrl);
    }

    public void addImageUrls(List<String> imageUrl) {
        this.imageUrls.addAll(imageUrl);
    }


}
