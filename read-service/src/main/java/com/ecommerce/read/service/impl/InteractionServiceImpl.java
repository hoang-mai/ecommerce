package com.ecommerce.read.service.impl;

import com.ecommerce.library.component.UserHelper;
import com.ecommerce.library.exception.NotFoundException;
import com.ecommerce.library.utils.FnCommon;
import com.ecommerce.library.utils.MessageError;
import com.ecommerce.library.utils.PageResponse;
import com.ecommerce.read.dto.CreateInteractionRequest;
import com.ecommerce.read.dto.InteractionDTO;
import com.ecommerce.read.dto.InteractionStatisticDTO;
import com.ecommerce.read.entity.Interaction;
import com.ecommerce.read.repository.InteractionRepository;
import com.ecommerce.read.service.InteractionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class InteractionServiceImpl implements InteractionService {

    private final InteractionRepository interactionRepository;
    private final UserHelper userHelper;

    @Override
    public void createInteraction(CreateInteractionRequest request) {
        String userId;
        if (FnCommon.isNotNullOrEmpty(request.getUserId())) {
            userId = request.getUserId();
        } else {
            userId = String.valueOf(userHelper.getCurrentUserId());
        }
        Optional<Interaction> existingInteraction = interactionRepository
            .findByUserIdAndProductIdAndInteractionType(
                userId,
                request.getProductId(),
                request.getInteractionType()
            );

        Interaction interaction;
        if (existingInteraction.isPresent()) {
            interaction = existingInteraction.get();
            interaction.incrementInteractionCount();
        } else {
            // Tạo mới tương tác
            interaction = Interaction.builder()
                .userId(userId)
                .productId(request.getProductId())
                .interactionType(request.getInteractionType())
                .metadata(convertToMetadata(request.getMetadata()))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        }

        interactionRepository.save(interaction);
    }

    @Override
    public List<InteractionDTO> getInteractionsByUserId(String userId) {
        log.info("Getting interactions for user: {}", userId);
        List<Interaction> interactions = interactionRepository.findByUserId(userId);
        return interactions.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    @Override
    public PageResponse<InteractionDTO> getInteractionsByProductId(String productId, int pageNo, int pageSize, String sortBy, String sortDir) {
        log.info("Getting interactions for product: {}", productId);

        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
            ? Sort.by(sortBy).ascending()
            : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
        Page<Interaction> interactionPage = interactionRepository.findByProductId(productId, pageable);

        List<InteractionDTO> content = interactionPage.getContent().stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());

        return PageResponse.<InteractionDTO>builder()
            .data(content)
            .pageNo(interactionPage.getNumber())
            .pageSize(interactionPage.getSize())
            .totalElements(interactionPage.getTotalElements())
            .totalPages(interactionPage.getTotalPages())
            .build();
    }

    @Override
    public List<InteractionDTO> getInteractionsByUserAndProduct(String userId, String productId) {
        log.info("Getting interactions for user: {} and product: {}", userId, productId);
        List<Interaction> interactions = interactionRepository.findByUserIdAndProductId(userId, productId);
        return interactions.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    @Override
    public List<InteractionDTO> getInteractionsByType(Interaction.InteractionType interactionType) {
        log.info("Getting interactions by type: {}", interactionType);
        List<Interaction> interactions = interactionRepository.findByInteractionType(interactionType);
        return interactions.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    @Override
    public List<InteractionDTO> getInteractionsByUserAndType(String userId, Interaction.InteractionType interactionType) {
        log.info("Getting interactions for user: {} and type: {}", userId, interactionType);
        List<Interaction> interactions = interactionRepository.findByUserIdAndInteractionType(userId, interactionType);
        return interactions.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    @Override
    public void deleteInteraction(String interactionId) {
        log.info("Deleting interaction: {}", interactionId);
        Interaction interaction = interactionRepository.findById(interactionId)
            .orElseThrow(() -> new NotFoundException(MessageError.INTERACTION_NOT_FOUND));
        interactionRepository.delete(interaction);
        log.info("Interaction deleted successfully");
    }

    @Override
    public void deleteInteractionByUserProductType(String userId, String productId, Interaction.InteractionType interactionType) {
        log.info("Deleting interaction for user: {}, product: {}, type: {}", userId, productId, interactionType);
        Optional<Interaction> interaction = interactionRepository
            .findByUserIdAndProductIdAndInteractionType(userId, productId, interactionType);

        interaction.ifPresent(interactionRepository::delete);
        log.info("Interaction deleted successfully");
    }

    @Override
    public InteractionDTO updateInteraction(String interactionId, InteractionDTO.InteractionMetadataDTO metadata) {
        log.info("Updating interaction: {}", interactionId);
        Interaction interaction = interactionRepository.findById(interactionId)
            .orElseThrow(() -> new NotFoundException(MessageError.INTERACTION_NOT_FOUND));

        interaction.setMetadata(convertToMetadata(metadata));
        interaction.setUpdatedAt(LocalDateTime.now());

        Interaction updatedInteraction = interactionRepository.save(interaction);
        log.info("Interaction updated successfully");

        return convertToDTO(updatedInteraction);
    }

    @Override
    public Long countInteractionsByProductAndType(String productId, Interaction.InteractionType interactionType) {
        log.info("Counting interactions for product: {} and type: {}", productId, interactionType);
        return interactionRepository.countByProductIdAndInteractionType(productId, interactionType);
    }

    @Override
    public Map<Interaction.InteractionType, Long> getProductInteractionStatistics(String productId) {
        log.info("Getting interaction statistics for product: {}", productId);
        Map<Interaction.InteractionType, Long> statistics = new HashMap<>();

        for (Interaction.InteractionType type : Interaction.InteractionType.values()) {
            Long count = interactionRepository.countByProductIdAndInteractionType(productId, type);
            statistics.put(type, count);
        }

        return statistics;
    }

    @Override
    public boolean hasUserInteractedWithProduct(String userId, String productId, Interaction.InteractionType interactionType) {
        log.info("Checking if user: {} has interacted with product: {} using type: {}", userId, productId, interactionType);
        return interactionRepository.existsByUserIdAndProductIdAndInteractionType(userId, productId, interactionType);
    }

    @Override
    public List<InteractionDTO> getUserFavoriteProducts(String userId) {
        log.info("Getting favorite products for user: {}", userId);
        List<Interaction> favorites = interactionRepository.findByUserIdAndInteractionType(
            userId,
            Interaction.InteractionType.FAVORITE
        );
        return favorites.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    @Override
    public List<InteractionDTO> getUserViewHistory(String userId) {
        log.info("Getting view history for user: {}", userId);
        List<Interaction> viewHistory = interactionRepository.findByUserIdAndInteractionType(
            userId,
            Interaction.InteractionType.VIEW
        );
        return viewHistory.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    @Override
    public List<InteractionStatisticDTO> getInteractionStatistics(LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Getting interaction statistics from {} to {}", startDate, endDate);
        List<Interaction> interactions = interactionRepository.findByCreatedAtBetween(startDate, endDate);

        // Group by product and interaction type
        Map<String, Map<Interaction.InteractionType, Long>> statistics = interactions.stream()
            .collect(Collectors.groupingBy(
                Interaction::getProductId,
                Collectors.groupingBy(
                    Interaction::getInteractionType,
                    Collectors.counting()
                )
            ));

        // Convert to DTO list
        List<InteractionStatisticDTO> result = new ArrayList<>();
        statistics.forEach((productId, typeCountMap) -> {
            typeCountMap.forEach((type, count) -> {
                result.add(InteractionStatisticDTO.builder()
                    .productId(productId)
                    .interactionType(type)
                    .count(count)
                    .build());
            });
        });

        return result;
    }

    // Helper methods
    private InteractionDTO convertToDTO(Interaction interaction) {
        return InteractionDTO.builder()
            .id(interaction.get_id())
            .userId(interaction.getUserId())
            .productId(interaction.getProductId())
            .interactionType(interaction.getInteractionType())
            .metadata(convertMetadataToDTO(interaction.getMetadata()))
            .createdAt(interaction.getCreatedAt())
            .updatedAt(interaction.getUpdatedAt())
            .build();
    }

    private InteractionDTO.InteractionMetadataDTO convertMetadataToDTO(Interaction.InteractionMetadata metadata) {
        if (metadata == null) {
            return null;
        }
        return InteractionDTO.InteractionMetadataDTO.builder()
            .sessionId(metadata.getSessionId())
            .ipAddress(metadata.getIpAddress())
            .userAgent(metadata.getUserAgent())
            .referrer(metadata.getReferrer())
            .duration(metadata.getDuration())
            .quantity(metadata.getQuantity())
            .build();
    }

    private Interaction.InteractionMetadata convertToMetadata(InteractionDTO.InteractionMetadataDTO metadataDTO) {
        if (metadataDTO == null) {
            return null;
        }
        return Interaction.InteractionMetadata.builder()
            .sessionId(metadataDTO.getSessionId())
            .ipAddress(metadataDTO.getIpAddress())
            .userAgent(metadataDTO.getUserAgent())
            .referrer(metadataDTO.getReferrer())
            .duration(metadataDTO.getDuration())
            .quantity(metadataDTO.getQuantity())
            .build();
    }
}

