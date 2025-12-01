package com.commerce.review.service;

import com.commerce.review.dto.ReqReviewReplyDTO;
import com.commerce.review.entity.ReviewReply;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ReviewReplyService {
    void createReply(ReqReviewReplyDTO reqReviewReplyDTO);
    void updateReply(Long replyId,ReqReviewReplyDTO reqReviewReplyDTO);
    void deleteReply(Long replyId);

}

