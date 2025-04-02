package com.ecom.webapp.controller.client;

import com.ecom.webapp.model.responseDto.ReviewRespone;
import com.ecom.webapp.repository.CommentRepository;
import com.ecom.webapp.repository.ReviewRepository;
import com.ecom.webapp.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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
    public ResponseEntity<List<ReviewRespone>> getAllReviews(
            @PathVariable(value = "storeId") String storeId,
            @RequestParam String productId) {
        if (storeId == null || storeId.isEmpty() ) {
            return new ResponseEntity<>(List.of(), HttpStatus.BAD_REQUEST);
        }

        int storeID = Integer.parseInt(storeId);

        // int check... if (...) {}

        return new ResponseEntity<>(this.reviewService.getReviews(storeID, productId), HttpStatus.OK);
    }

}
