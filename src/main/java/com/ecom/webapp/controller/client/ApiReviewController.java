package com.ecom.webapp.controller.client;

import com.ecom.webapp.model.Review;
import com.ecom.webapp.model.dto.ReviewDto;
import com.ecom.webapp.model.responseDto.ReviewResponse;
import com.ecom.webapp.repository.CommentRepository;
import com.ecom.webapp.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/secure/reviews")
public class ApiReviewController {

    @Autowired
    private ReviewService reviewService;
    @Autowired
    private CommentRepository commentRepository;

    /*
        /reviews?storeId=12$product
        /reviews/{storeId}
        /reviews/{storeId}?productId=1
        /products/{productId}/reviews


     */
    @GetMapping("/{storeId}")
    public ResponseEntity<List<ReviewResponse>> getAllReviews(
            @PathVariable(value = "storeId") String storeId,
            @RequestParam(value = "productId", required = false, defaultValue = "") String productId) {

        System.out.println("Product Id: " + productId);
        System.out.println("Store Id: " + storeId);

        int storeID;
        try {
            storeID = Integer.parseInt(storeId);
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body(List.of());
        }

        Integer prodID = null;
        if (productId != null && !productId.isEmpty()) {
            try {
                prodID = Integer.parseInt(productId);
            } catch (NumberFormatException e) {
                return ResponseEntity.badRequest().body(List.of()); // Trả về HTTP 400 nếu productId không hợp lệ
            }
        }


        List<ReviewResponse> reviews = this.reviewService.getReviews(storeID, prodID);
        return new ResponseEntity<>(reviews, HttpStatus.OK);
    }

    @PostMapping("/add")
    public ResponseEntity<?> createReview(
            @Valid @RequestBody ReviewDto reviewDto,
            @AuthenticationPrincipal UserDetails userDetails) {


        //Tam thoi 
        String username = reviewDto.getUsername();
        reviewDto.setUsername(username);
        System.out.println(reviewDto);

        this.reviewService.addReview(reviewDto);

        return ResponseEntity.ok("Review added successfully!");
    }

    // Khong cho xoa review. Vi chu cua hang xoa cac bai bad review di thi sao
}
