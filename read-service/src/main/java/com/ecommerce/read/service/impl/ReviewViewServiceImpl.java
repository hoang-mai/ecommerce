package com.ecommerce.read.service.impl;

import com.ecommerce.library.component.UserHelper;
import com.ecommerce.library.kafka.event.review.CreateReviewViewEvent;
import com.ecommerce.library.kafka.event.review.DeleteReviewViewEvent;
import com.ecommerce.library.kafka.event.review.UpdateReviewViewEvent;
import com.ecommerce.library.kafka.event.review.CreateReviewReplyEvent;
import com.ecommerce.library.kafka.event.review.UpdateReviewReplyEvent;
import com.ecommerce.library.kafka.event.review.DeleteReviewReplyEvent;
import com.ecommerce.library.kafka.event.user.UpdateAvatarUserEvent;
import com.ecommerce.library.kafka.event.user.UpdateUserEvent;
import com.ecommerce.library.utils.PageResponse;
import com.ecommerce.read.entity.ReviewView;
import com.ecommerce.read.repository.ReviewViewRepository;
import com.ecommerce.read.repository.impl.ReviewViewRepositoryImpl;
import com.ecommerce.read.service.FileService;
import com.ecommerce.read.service.ProductViewService;
import com.ecommerce.read.service.ReviewViewService;
import com.ecommerce.read.repository.UserViewRepository;
import com.ecommerce.read.entity.UserView;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReviewViewServiceImpl implements ReviewViewService {

    private final ReviewViewRepository reviewViewRepository;
    private final ProductViewService productViewService;
    private final ReviewViewRepositoryImpl reviewViewRepositoryImpl;
    private final FileService fileService;
    private final UserHelper userHelper;
    private final UserViewRepository userViewRepository;

    @Override
    public void createReviewView(CreateReviewViewEvent createReviewViewEvent) {
        if (createReviewViewEvent == null) return;
        UserView userView = userViewRepository.findById(String.valueOf(createReviewViewEvent.getUserId())).orElse(null);
        ReviewView rv = ReviewView.builder()
            ._id(String.valueOf(createReviewViewEvent.getReviewId()))
            .orderItemId(String.valueOf(createReviewViewEvent.getOrderItemId()))
            .productId(String.valueOf(createReviewViewEvent.getProductId()))
            .productName(createReviewViewEvent.getProductName())
            .productVariantId(String.valueOf(createReviewViewEvent.getProductVariantId()))
            .userId(String.valueOf(createReviewViewEvent.getUserId()))
            .fullName(userView.getFullName())
            .avatarUrl(userView.getAvatarUrl())
            .ownerId(String.valueOf(createReviewViewEvent.getOwnerId()))
            .shopId(String.valueOf(createReviewViewEvent.getShopId()))
            .rating(createReviewViewEvent.getRating())
            .comment(createReviewViewEvent.getComment())
            .imageUrls(createReviewViewEvent.getImageUrls())
            .attributes(createReviewViewEvent.getAttributes())
            .createdAt(createReviewViewEvent.getCreatedAt())
            .build();
        reviewViewRepository.save(rv);
        productViewService.updateRating(createReviewViewEvent.getProductId(), createReviewViewEvent.getRating(), false, null, false);
    }

    @Override
    public void updateReviewView(UpdateReviewViewEvent updateReviewViewEvent) {
        if (updateReviewViewEvent == null) return;
        Optional<ReviewView> opt = reviewViewRepository.findById(String.valueOf(updateReviewViewEvent.getReviewId()));
        if (opt.isEmpty()) return;
        ReviewView rv = opt.get();
        productViewService.updateRating(
            Long.parseLong(rv.getProductId()),
            updateReviewViewEvent.getRating(),
            true,
            rv.getRating(),
            false
        );
        rv.setRating(updateReviewViewEvent.getRating());
        rv.setComment(updateReviewViewEvent.getComment());
        rv.setImageUrls(updateReviewViewEvent.getImageUrls());
        rv.setAttributes(updateReviewViewEvent.getAttributes());
        rv.setIsUpdated(true);
        reviewViewRepository.save(rv);
    }

    @Override
    public void deleteReviewView(DeleteReviewViewEvent deleteReviewViewEvent) {
        if (deleteReviewViewEvent == null) return;
        Optional<ReviewView> opt = reviewViewRepository.findById(String.valueOf(deleteReviewViewEvent.getReviewId()));
        if (opt.isEmpty()) return;
        ReviewView rv = opt.get();
        productViewService.updateRating(
            Long.parseLong(rv.getProductId()),
            null,
            true,
            rv.getRating(),
            true
        );
        reviewViewRepository.deleteById(String.valueOf(deleteReviewViewEvent.getReviewId()));
    }

    @Override
    public void createReviewReply(CreateReviewReplyEvent createReviewReplyEvent) {
        if (createReviewReplyEvent == null) return;
        String reviewId = String.valueOf(createReviewReplyEvent.getReviewId());
        Optional<ReviewView> opt = reviewViewRepository.findById(reviewId);
        if (opt.isEmpty()) return;
        ReviewView rv = opt.get();
        ReviewView.ReviewReplyView replyView = ReviewView.ReviewReplyView.builder()
            .replyId(String.valueOf(createReviewReplyEvent.getReplyId()))
            .replierId(String.valueOf(createReviewReplyEvent.getReplierId()))
            .content(createReviewReplyEvent.getContent())
            .createdAt(createReviewReplyEvent.getCreatedAt())
            .build();
        rv.setReviewReplyView(replyView);
        reviewViewRepository.save(rv);
    }

    @Override
    public void updateReviewReply(UpdateReviewReplyEvent updateReviewReplyEvent) {
        if (updateReviewReplyEvent == null) return;
        String reviewId = String.valueOf(updateReviewReplyEvent.getReviewId());
        Optional<ReviewView> opt = reviewViewRepository.findById(reviewId);
        if (opt.isEmpty()) return;
        ReviewView rv = opt.get();
        ReviewView.ReviewReplyView r = rv.getReviewReplyView();
        if (r == null) return;
        if (!String.valueOf(updateReviewReplyEvent.getReplyId()).equals(r.getReplyId())) return;
        r.setContent(updateReviewReplyEvent.getContent());
        rv.setReviewReplyView(r);
        reviewViewRepository.save(rv);
    }

    @Override
    public void deleteReviewReply(DeleteReviewReplyEvent deleteReviewReplyEvent) {
        if (deleteReviewReplyEvent == null) return;
        String reviewId = String.valueOf(deleteReviewReplyEvent.getReviewId());
        Optional<ReviewView> opt = reviewViewRepository.findById(reviewId);
        if (opt.isEmpty()) return;
        ReviewView rv = opt.get();
        ReviewView.ReviewReplyView r = rv.getReviewReplyView();
        if (r == null) return;
        if (String.valueOf(deleteReviewReplyEvent.getReplyId()).equals(r.getReplyId())) {
            rv.setReviewReplyView(null);
        }
        reviewViewRepository.save(rv);
    }

    @Override
    public PageResponse<ReviewView> getReviewsByProductId(Long productId, String stars, Boolean isOwner, Long shopId, Boolean isReply, int pageNo, int pageSize, String sortBy, String sortDir) {
        Long ownerId = null;
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
            ? Sort.by(sortBy).ascending()
            : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
        if (Boolean.TRUE.equals(isOwner)) {
            ownerId = userHelper.getCurrentUserId();
        }
        Page<ReviewView> reviewViewPage = reviewViewRepositoryImpl.getReviewsByProductId(
            productId,
            stars,
            ownerId,
            shopId,
            isReply,
            pageable
        );
        return PageResponse.<ReviewView>builder()
            .data(reviewViewPage.getContent().stream().peek(reviewView -> {
                reviewView.setAvatarUrl(fileService.getPresignedUrl(reviewView.getAvatarUrl()));
                reviewView.setImageUrls(
                    reviewView.getImageUrls().stream()
                        .map(fileService::getPresignedUrl)
                        .toList()
                );
            }).toList())
            .pageNo(reviewViewPage.getNumber())
            .pageSize(reviewViewPage.getSize())
            .totalElements(reviewViewPage.getTotalElements())
            .totalPages(reviewViewPage.getTotalPages())
            .hasNextPage(reviewViewPage.hasNext())
            .hasPreviousPage(reviewViewPage.hasPrevious())
            .build();
    }

    @Override
    public ReviewView getReviewByOrderItemId(Long orderItemId) {
        Optional<ReviewView> reviewViewOptional = reviewViewRepository.findByOrderItemId(String.valueOf(orderItemId));
        if (reviewViewOptional.isPresent()) {
            ReviewView reviewView = reviewViewOptional.get();
            reviewView.setImageUrls(
                reviewView.getImageUrls().stream()
                    .map(fileService::getPresignedUrl)
                    .toList()
            );
            return reviewView;
        } else {
            return null;
        }

    }

    @Override
    public void updateAvatarUserInReviews(UpdateAvatarUserEvent updateAvatarUserEvent) {
        reviewViewRepositoryImpl.updateAvatarUserInReviews(
            String.valueOf(updateAvatarUserEvent.getUserId()),
            updateAvatarUserEvent.getAvatarUrl()
        );
    }

    @Override
    public void updateUserInReviews(UpdateUserEvent updateUserEvent) {
        reviewViewRepositoryImpl.updateUserInReviews(
            String.valueOf(updateUserEvent.getUserId()),
            updateUserEvent.getFullName()
        );
    }
}
