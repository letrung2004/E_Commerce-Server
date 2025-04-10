package com.ecom.webapp.service.impl;

import com.ecom.webapp.model.*;
import com.ecom.webapp.model.dto.ReviewDto;
import com.ecom.webapp.model.responseDto.ReviewResponse;
import com.ecom.webapp.repository.*;
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
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CommentRepository commentRepository;

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
    public void addReview(ReviewDto reviewDto) {
//                "userId": 3,
//                "storeId": 8,
//                "productId": 1,
//                "commentId": 1,
//                "dateCreated": null,
//                "rate": 5
        User user = this.userRepository.getUserByUsername(reviewDto.getUsername());
        if (user == null) {
            throw new EntityNotFoundException("User not found with id " + reviewDto.getUserId());
        }
        Product product = this.productRepository.getProductById(reviewDto.getProductId());
        if (product == null) {
            throw new EntityNotFoundException("Product not found with id " + reviewDto.getProductId());
        }

        Review review = new Review();
        review.setUser(user);
        review.setProduct(product);
        review.setStore(product.getStore());
        review.setRate(reviewDto.getRate());

        if (reviewDto.getComment() != null) {
            Comment comment = new Comment();
            comment.setContent(reviewDto.getComment());
            comment.setUser(user);
            this.commentRepository.createComment(comment);
            review.setComment(comment);
        }

        this.reviewRepository.addReview(review);

    }

    @Override
    public void updateReview(Review review) {

    }

    @Override
    public void deleteReview(Review review) {

    }
}
