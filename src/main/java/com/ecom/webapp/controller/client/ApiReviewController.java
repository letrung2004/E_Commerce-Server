package com.ecom.webapp.controller.client;

import com.ecom.webapp.model.dto.ReviewDto;
import com.ecom.webapp.model.responseDto.ReviewResponse;
import com.ecom.webapp.repository.CommentRepository;
import com.ecom.webapp.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ApiReviewController {

    @Autowired
    private ReviewService reviewService;
    @Autowired
    private CommentRepository commentRepository;

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }


    /*
        /reviews?storeId=12$product
        /reviews/{storeId}
        /reviews/{storeId}?productId=1
        /products/{productId}/reviews


     */
    @GetMapping("/reviews/{storeId}")
    public ResponseEntity<List<ReviewResponse>> getAllReviews(
            @PathVariable(value = "storeId") String storeId,
            @RequestParam(value = "productId", required = false, defaultValue = "") String productId,
            @RequestParam(value = "page", required = false, defaultValue = "1") int page) {

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


        List<ReviewResponse> reviews = this.reviewService.getReviews(storeID, prodID, page);
        return new ResponseEntity<>(reviews, HttpStatus.OK);
    }

    @PostMapping("/secure/reviews/add")
    public ResponseEntity<?> createReview(
            @Valid @RequestBody ReviewDto reviewDto,
            Principal principal) {

        String username = principal.getName();
        reviewDto.setUsername(username);
        System.out.println(reviewDto);

        this.reviewService.addReview(reviewDto);

        return ResponseEntity.ok("Review added successfully!");
    }

    // Khong cho xoa review. Vi chủ cua hang xoa cac bai bad review di thi sao
}
