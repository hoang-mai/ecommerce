package com.ecommerce.read.service.impl;

import com.ecommerce.library.kafka.event.review.CreateReviewViewEvent;
import com.ecommerce.library.kafka.event.review.DeleteReviewViewEvent;
import com.ecommerce.library.kafka.event.review.UpdateReviewViewEvent;
import com.ecommerce.library.kafka.event.review.CreateReviewReplyEvent;
import com.ecommerce.library.kafka.event.review.UpdateReviewReplyEvent;
import com.ecommerce.library.kafka.event.review.DeleteReviewReplyEvent;
import com.ecommerce.library.utils.PageResponse;
import com.ecommerce.read.entity.ReviewView;
import com.ecommerce.read.repository.ReviewViewRepository;
import com.ecommerce.read.repository.impl.ReviewViewRepositoryImpl;
import com.ecommerce.read.service.FileService;
import com.ecommerce.read.service.ProductViewService;
import com.ecommerce.read.service.ReviewViewService;
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

    @Override
    public void createReviewView(CreateReviewViewEvent createReviewViewEvent) {
        if (createReviewViewEvent == null) return;
        ReviewView rv = ReviewView.builder()
                ._id(String.valueOf(createReviewViewEvent.getReviewId()))
                .orderItemId(String.valueOf(createReviewViewEvent.getOrderItemId()))
                .productId(String.valueOf(createReviewViewEvent.getProductId()))
                .productVariantId(String.valueOf(createReviewViewEvent.getProductVariantId()))
                .userId(String.valueOf(createReviewViewEvent.getUserId()))
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
                0.0,
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
    public PageResponse<ReviewView> getReviewsByProductId(Long productId, Integer stars, int pageNo, int pageSize, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        Page<ReviewView> reviewViewPage = reviewViewRepositoryImpl.getReviewsByProductId(
                String.valueOf(productId),
                stars,
                pageable
        );
        return PageResponse.<ReviewView>builder()
                .data(reviewViewPage.getContent().stream().peek(reviewView -> reviewView.setImageUrls(
                        reviewView.getImageUrls().stream()
                                .map(fileService::getPresignedUrl)
                                .toList()
                )).toList())
                .pageNo(reviewViewPage.getNumber())
                .pageSize(reviewViewPage.getSize())
                .totalElements(reviewViewPage.getTotalElements())
                .totalPages(reviewViewPage.getTotalPages())
                .hasNextPage(reviewViewPage.hasNext())
                .hasPreviousPage(reviewViewPage.hasPrevious())
                .build();
    }
}
