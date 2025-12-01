package com.ecommerce.review.dto;

import com.ecommerce.review.entity.Review;
import com.ecommerce.library.enumeration.RatingNumber;
import com.ecommerce.library.utils.MessageError;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * DTO for {@link Review}
 */
@AllArgsConstructor
@Getter
public class ReqReviewDTO implements Serializable {
    private final RatingNumber rating;
    @NotBlank(message = MessageError.COMMENT_NOT_BLANK)
    private final String comment;
    @NotNull(message = MessageError.ORDER_ITEM_ID_NOT_NULL)
    @Positive(message = MessageError.ORDER_ITEM_ID_POSITIVE)
    private final Long orderItemId;
    @NotNull(message = MessageError.PRODUCT_ID_NOT_NULL)
    @Positive(message = MessageError.PRODUCT_ID_POSITIVE)
    private final Long productId;
    @NotNull(message = MessageError.PRODUCT_VARIANT_ID_NOT_NULL)
    @Positive(message = MessageError.PRODUCT_VARIANT_ID_POSITIVE)
    private final Long productVariantId;

    @NotNull(message = MessageError.PRODUCT_ATTRIBUTES_NOT_NULL)
    private final Map<String, String> attributes;

    private List<String> deletedImageUrls;
}