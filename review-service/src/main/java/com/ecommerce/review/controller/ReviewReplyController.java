package com.ecommerce.review.controller;

import com.ecommerce.review.dto.ReqReviewReplyDTO;
import com.ecommerce.review.service.ReviewReplyService;
import com.ecommerce.library.component.MessageService;
import com.ecommerce.library.utils.BaseResponse;
import com.ecommerce.library.utils.Constant;
import com.ecommerce.library.utils.MessageSuccess;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(Constant.REVIEW_REPLY)
@RequiredArgsConstructor
public class ReviewReplyController {
    private final ReviewReplyService reviewReplyService;
    private final MessageService messageService;

    @PostMapping
    public ResponseEntity<BaseResponse<Void>> createReply(@RequestBody ReqReviewReplyDTO request) {
        reviewReplyService.createReply(request);
        return ResponseEntity.ok(BaseResponse.<Void>builder()
                .statusCode(HttpStatus.CREATED.value())
                .message(messageService.getMessage(MessageSuccess.REVIEW_REPLY_CREATED_SUCCESS))
                .build());
    }

    @PatchMapping("/{replyId}")
    public ResponseEntity<BaseResponse<Void>> updateReply(@PathVariable Long replyId,
                                                                @RequestBody ReqReviewReplyDTO request) {
        reviewReplyService.updateReply(replyId,request);
        return ResponseEntity.ok(BaseResponse.<Void>builder()
                .statusCode(200)
                .message(messageService.getMessage(MessageSuccess.REVIEW_REPLY_UPDATED_SUCCESS))
                .build());
    }

    @DeleteMapping("/{replyId}")
    public ResponseEntity<BaseResponse<Void>> deleteReply(@PathVariable Long replyId) {
        reviewReplyService.deleteReply(replyId);
        return ResponseEntity.ok(BaseResponse.<Void>builder()
                .statusCode(200)
                .message(messageService.getMessage(MessageSuccess.REVIEW_REPLY_DELETED_SUCCESS))
                .build());
    }

}
