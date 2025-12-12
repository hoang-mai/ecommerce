package com.ecommerce.read.dto;

import com.ecommerce.read.entity.Interaction;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InteractionDTO {

    @JsonProperty("id")
    private String id;

    private String userId;

    private String productId;

    private Interaction.InteractionType interactionType;

    private InteractionMetadataDTO metadata;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InteractionMetadataDTO {
        private String sessionId;
        private String ipAddress;
        private String userAgent;
        private String referrer;
        private Long duration;
        private Integer quantity;
    }
}

