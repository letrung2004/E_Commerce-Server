package com.ecom.webapp.service;

import com.ecom.webapp.model.Review;
import com.ecom.webapp.model.responseDto.ReviewRespone;

import java.util.List;
import java.util.Map;

public interface ReviewService {
    List<ReviewRespone> getReviews(int storeId, String productId);
    void addReview(Review review);
    void updateReview(Review review);
    void deleteReview(Review review);
}
