package com.ecom.webapp.service.impl;

import com.ecom.webapp.model.Review;
import com.ecom.webapp.model.responseDto.ReviewRespone;
import com.ecom.webapp.service.ReviewService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Transactional
public class ReviewServiceImpl implements ReviewService {
    @Override
    public List<ReviewRespone> getReviews(int storeId, String productId) {
        return List.of();
    }

    @Override
    public void addReview(Review review) {

    }

    @Override
    public void updateReview(Review review) {

    }

    @Override
    public void deleteReview(Review review) {

    }
}
