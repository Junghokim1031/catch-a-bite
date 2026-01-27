package com.deliveryapp.catchabite.controller;

/**
 * ReviewController: 사용자 리뷰 관리 HTTP 엔드포인트
 * * Description: 사용자가 주문에 대한 리뷰를 작성, 조회, 수정, 삭제합니다.
 * * 주요 기능:
 * 1. 리뷰 작성 (createReview) - 주문에 대한 리뷰 등록 [POST, Return: ReviewDTO]
 * 2. 리뷰 조회 (getReview) - 특정 리뷰 상세 조회 [GET, Return: ReviewDTO]
 * 3. 리뷰 수정 (updateReview) - 작성한 리뷰 내용 수정 [PUT, Return: ReviewDTO]
 * 4. 리뷰 삭제 (deleteReview) - 리뷰 삭제 [DELETE, Return: Void]
 */

import com.deliveryapp.catchabite.common.response.ApiResponse;
import com.deliveryapp.catchabite.dto.ReviewDTO;
import com.deliveryapp.catchabite.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/appuser/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    /**
     * 완료된 주문에 대해 새로운 리뷰를 작성합니다.
     * POST /api/v1/appuser/reviews
     */
    @PostMapping
    public ResponseEntity<ApiResponse<ReviewDTO>> createReview(@RequestParam Long storeOrderId, 
                                                               @RequestParam BigDecimal rating, 
                                                               @RequestParam String content) {
        
        // 1. 주문 ID 유효성 체크: null이거나 0 이하일 경우 에러 반환
        if (storeOrderId == null || storeOrderId <= 0) {
            return ResponseEntity.badRequest().body(ApiResponse.fail("BAD_REQUEST","유효하지 않은 주문 ID입니다."));
        }

        // 2. 평점 유효성 체크: 0점 미만이거나 5점을 초과할 경우 에러 반환
        if (rating == null || rating.compareTo(BigDecimal.ZERO) < 0 || rating.compareTo(new BigDecimal("5")) > 0) {
            return ResponseEntity.badRequest().body(ApiResponse.fail("BAD_REQUEST","평점은 0점에서 5점 사이여야 합니다."));
        }

        // 3. 리뷰 내용 유효성 체크: 내용이 비어있거나 공백만 있을 경우 에러 반환
        if (content == null || content.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(ApiResponse.fail("BAD_REQUEST","리뷰 내용을 입력해주세요."));
        }

        ReviewDTO createdReview = reviewService.createReview(storeOrderId, rating, content);
        return ResponseEntity.ok(ApiResponse.ok(createdReview));
    }

    /**
     * 특정 리뷰의 상세 정보를 조회합니다.
     * GET /api/v1/appuser/reviews/{reviewId}
     */
    @GetMapping("/{reviewId}")
    public ResponseEntity<ApiResponse<ReviewDTO>> getReview(@PathVariable Long reviewId) {
        ReviewDTO review = reviewService.getReview(reviewId);
        return ResponseEntity.ok(ApiResponse.ok(review));
    }

    /**
     * 기존 리뷰의 평점이나 내용을 수정합니다.
     * PUT /api/v1/appuser/reviews/{reviewId}
     */
    @PutMapping("/{reviewId}")
    public ResponseEntity<ApiResponse<ReviewDTO>> updateReview(@PathVariable Long reviewId, @RequestBody ReviewDTO dto) {
        ReviewDTO updatedReview = reviewService.updateReview(reviewId, dto);
        return ResponseEntity.ok(ApiResponse.ok(updatedReview));
    }

    /**
     * 작성한 리뷰를 삭제합니다.
     * DELETE /api/v1/appuser/reviews/{reviewId}
     */
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<ApiResponse<Void>> deleteReview(@PathVariable Long reviewId) {
        reviewService.deleteReview(reviewId);
        return ResponseEntity.ok(ApiResponse.ok(null,"Review deleted successfully"));
    }
}