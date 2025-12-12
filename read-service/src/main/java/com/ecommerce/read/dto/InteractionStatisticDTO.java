package com.ecommerce.read.dto;

import com.ecommerce.read.entity.Interaction;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InteractionStatisticDTO {

    private String productId;

    private Interaction.InteractionType interactionType;

    private Long count;

    private String productName;
}

