package com.ecom.webapp.service;

import com.ecom.webapp.model.Review;
import com.ecom.webapp.model.dto.ReviewDto;
import com.ecom.webapp.model.responseDto.ReviewResponse;

import java.util.List;

public interface ReviewService {
    List<ReviewResponse> getReviews(int storeId, Integer productId);
    void addReview(ReviewDto reviewDto);
    void updateReview(Review review);
    void deleteReview(Review review);
}
