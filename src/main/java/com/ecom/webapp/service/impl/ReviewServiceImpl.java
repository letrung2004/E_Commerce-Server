package com.ecom.webapp.service.impl;

import com.ecom.webapp.model.Review;
import com.ecom.webapp.model.Store;
import com.ecom.webapp.model.responseDto.ReviewResponse;
import com.ecom.webapp.repository.ReviewRepository;
import com.ecom.webapp.repository.StoreRepository;
import com.ecom.webapp.service.ReviewService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ReviewServiceImpl implements ReviewService {

    @Autowired
    private StoreRepository storeRepository;
    @Autowired
    private ReviewRepository reviewRepository;

    @Override
    public List<ReviewResponse> getReviews(int storeId, Integer productId) {
        Store store = this.storeRepository.getStoreById(storeId);
        if (store == null) {
            throw new EntityNotFoundException("Store not found with id " + storeId);
        }
        List<Review> reviews = this.reviewRepository.getReviews(store, productId);
       return reviews.stream().map(ReviewResponse::new).toList();
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
