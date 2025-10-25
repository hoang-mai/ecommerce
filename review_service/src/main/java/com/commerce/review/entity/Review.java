package com.commerce.review.entity;


import com.ecommerce.library.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

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
    private Integer rating;

    @Column(name="comment", length = 2000)
    private String comment;

    @Column(name="product_variant_id", nullable = false)
    private Long productVariantId;

    @Column(name="user_id", nullable = false)
    private Long userId;


    @OneToOne(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
    private ReviewReply reviewReply;
}
