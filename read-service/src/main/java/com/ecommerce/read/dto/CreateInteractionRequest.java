package com.ecommerce.read.dto;

import com.ecommerce.read.entity.Interaction;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateInteractionRequest {

    private String userId;

    @NotBlank(message = "Product ID is required")
    private String productId;

    @NotNull(message = "Interaction type is required")
    private Interaction.InteractionType interactionType;

    private InteractionDTO.InteractionMetadataDTO metadata;
}

