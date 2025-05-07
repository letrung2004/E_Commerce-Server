package com.ecom.webapp.repository;

import com.ecom.webapp.model.Review;
import com.ecom.webapp.model.Store;

import java.util.List;
import java.util.Map;

public interface ReviewRepository {
    List<Review> getReviews(Store store, Integer productId, int page);
    void addReview(Review review);
    void updateReview(Review review);
    void deleteReview(Review review);

}
