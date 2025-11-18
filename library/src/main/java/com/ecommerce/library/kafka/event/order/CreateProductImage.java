package com.ecommerce.library.kafka.event.order;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateProductImage {
    private Long productImageId;
    private String imageUrl;
}
