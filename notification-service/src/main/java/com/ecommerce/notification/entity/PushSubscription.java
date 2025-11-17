package com.ecommerce.notification.entity;

import com.ecommerce.library.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "push_subscriptions")
public class PushSubscription extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "push_subscription_id")
    private Long pushSubscriptionId;

    @Column(name = "user_id")
    private Long userId;

    private String endpoint;

    @Column(length = 512)
    private String p256dh;

    @Column(length = 128)
    private String auth; // authentication secret
    private boolean active;
}
