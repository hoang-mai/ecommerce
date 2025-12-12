package com.ecommerce.review.service;

import com.ecommerce.review.dto.ReqReviewReplyDTO;

public interface ReviewReplyService {
    void createReply(ReqReviewReplyDTO reqReviewReplyDTO);
    void updateReply(Long replyId,ReqReviewReplyDTO reqReviewReplyDTO);
    void deleteReply(Long replyId);

}

