package com.commerce.review.entity;


import com.ecommerce.library.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

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

    @Column(name="rating", nullable = false)
    private Double rating;

    @Column(name="comment", length = 2000)
    private String comment;

    @Column(name="order_id", nullable = false)
    private Long orderId;

    @Column(name="product_id", nullable = false)
    private Long productId;


    @Column(name="product_variant_id", nullable = false)
    private Long productVariantId;

    @Column(name="user_id", nullable = false)
    private Long userId;

    @CollectionTable(name = "review_images", joinColumns = @JoinColumn(name = "review_id"))
    @ElementCollection
    @Column(name = "image_url")
    @OrderColumn(name = "image_order")
    private List<String> imageUrls;


    @OneToOne(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
    private ReviewReply reviewReply;
}
