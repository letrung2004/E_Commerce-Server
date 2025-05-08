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
    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Override
    public List<ReviewResponse> getReviews(int storeId, Integer productId, int page) {
        Store store = this.storeRepository.getStoreById(storeId);
        if (store == null) {
            throw new EntityNotFoundException("Store not found with id " + storeId);
        }
        List<Review> reviews = this.reviewRepository.getReviews(store, productId, page);
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


//        { from front-end
//            "orderDetailIds": [1,2],
//            "comments" : ["ok","good"],
//            "rates" : [4,5],
//        }
        User user = this.userRepository.getUserByUsername(reviewDto.getUsername());
        if (user == null) {
            throw new EntityNotFoundException("User not found with username " + reviewDto.getUsername());
        }
        List<Integer> orderDetailIds = reviewDto.getOrderDetailIds();
        List<Integer> rates = reviewDto.getRates();
        List<String> comments = reviewDto.getComments();

        for (int i = 0; i < orderDetailIds.size(); i++) {
            int orderDetailId = orderDetailIds.get(i);
            OrderDetail orderDetail = this.orderDetailRepository.getOrderDetailById(orderDetailId);

            if (orderDetail == null) {
                throw new EntityNotFoundException("orderDetail not found with id " + orderDetailId);
            }
            if (orderDetail.isEvaluated()){
                throw new EntityNotFoundException("orderDetail is already evaluated, id " + orderDetailId);
            }

            Review review = new Review();
            review.setUser(user);
            review.setProduct(orderDetail.getProduct());
            review.setStore(orderDetail.getProduct().getStore());
            review.setOrder(orderDetail.getOrder());
            review.setRate(rates.get(i));

            if (reviewDto.getComments() != null && i < reviewDto.getComments().size()) {
                Comment comment = new Comment();
                comment.setContent(comments.get(i));
                comment.setUser(user);
                this.commentRepository.createComment(comment);
                review.setComment(comment);
            }

            this.reviewRepository.addReview(review);
            orderDetail.setEvaluated(true);
            this.orderDetailRepository.updateOrderDetail(orderDetail);
        }

    }


//    @Override
//    public void addReview(ReviewDto reviewDto) {
////                "userId": 3,
////                "storeId": 8,
////                "productId": 1,
////                "commentId": 1,
////                "dateCreated": null,
////                "rate": 5
//        User user = this.userRepository.getUserByUsername(reviewDto.getUsername());
//        if (user == null) {
//            throw new EntityNotFoundException("User not found with username " + reviewDto.getUsername());
//        }
//        Product product = this.productRepository.getProductById(reviewDto.getProductId());
//        if (product == null) {
//            throw new EntityNotFoundException("Product not found with id " + reviewDto.getProductId());
//        }
//
//        Review review = new Review();
//        review.setUser(user);
//        review.setProduct(product);
//        review.setStore(product.getStore());
//        review.setRate(reviewDto.getRate());
//
//        if (reviewDto.getComment() != null) {
//            Comment comment = new Comment();
//            comment.setContent(reviewDto.getComment());
//            comment.setUser(user);
//            this.commentRepository.createComment(comment);
//            review.setComment(comment);
//        }
//
//        this.reviewRepository.addReview(review);
//
//    }

    @Override
    public void updateReview(Review review) {

    }

    @Override
    public void deleteReview(Review review) {

    }
}
