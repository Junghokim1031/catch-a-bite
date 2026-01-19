package com.deliveryapp.catchabite.controller;

import com.deliveryapp.catchabite.common.response.ApiResponse;
import com.deliveryapp.catchabite.dto.ReviewDTO;
import com.deliveryapp.catchabite.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/appuser/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<ApiResponse<ReviewDTO>> createReview(@RequestParam Long storeOrderId, 
                                                               @RequestParam BigDecimal rating, 
                                                               @RequestParam String content) {
        ReviewDTO createdReview = reviewService.createReview(storeOrderId, rating, content);
        return ResponseEntity.ok(ApiResponse.ok(createdReview));
    }

    @GetMapping("/{reviewId}")
    public ResponseEntity<ApiResponse<ReviewDTO>> getReview(@PathVariable Long reviewId) {
        ReviewDTO review = reviewService.getReview(reviewId);
        return ResponseEntity.ok(ApiResponse.ok(review));
    }

    @PutMapping("/{reviewId}")
    public ResponseEntity<ApiResponse<ReviewDTO>> updateReview(@PathVariable Long reviewId, @RequestBody ReviewDTO dto) {
        ReviewDTO updatedReview = reviewService.updateReview(reviewId, dto);
        return ResponseEntity.ok(ApiResponse.ok(updatedReview));
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<ApiResponse<Void>> deleteReview(@PathVariable Long reviewId) {
        reviewService.deleteReview(reviewId);
        return ResponseEntity.ok(ApiResponse.okMessage("Review deleted successfully"));
    }
}