package com.ecom.webapp.controller.client;

import com.ecom.webapp.model.responseDto.ReviewResponse;
import com.ecom.webapp.repository.CommentRepository;
import com.ecom.webapp.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
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
    @GetMapping("/reviews/{storeId}")
    public ResponseEntity<List<ReviewResponse>> getAllReviews(
            @PathVariable(value = "storeId") String storeId,
            @RequestParam(value = "productId", required = false, defaultValue = "") String productId
    ) {
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


}
