package com.ecommerce.library.kafka.event.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UploadProductImageEvent {
    private Long productId;
    private List<String> imageUrls;
}
