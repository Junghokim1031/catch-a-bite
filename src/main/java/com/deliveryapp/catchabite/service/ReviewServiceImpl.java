package com.deliveryapp.catchabite.service;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.deliveryapp.catchabite.converter.ReviewConverter;
import com.deliveryapp.catchabite.dto.ReviewDTO;
import com.deliveryapp.catchabite.entity.Review;
import com.deliveryapp.catchabite.entity.StoreOrder;
import com.deliveryapp.catchabite.repository.ReviewRepository;
import com.deliveryapp.catchabite.repository.StoreOrderRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
@RequiredArgsConstructor  // Replaces manual field injection
@Transactional(readOnly = true)  // Default read-only for better perf
public class ReviewServiceImpl implements ReviewService {
    
    private final ReviewRepository reviewRepository;
    private final StoreOrderRepository storeOrderRepository;
    private final ReviewConverter reviewConverter;

    @Override
    @Transactional  // Overrides read-only for write
    public ReviewDTO createReview(Long storeOrderId, BigDecimal reviewRating, String reviewContent) {
        // 1. Fetch and validate order (ensures exists, handles lazy init safely)
        if(storeOrderId == null || storeOrderId > 0){
            throw new IllegalArgumentException("ReviewServiceImpl - createReview - storeOrderId " + storeOrderId + "가 NULL입니다.");
        }
        StoreOrder order = storeOrderRepository.findById(storeOrderId)
                .orElseThrow(() -> new IllegalArgumentException(
                    "ReviewServiceImpl - createReview - storeOrderId " + storeOrderId + "가 존재하지 않습니다."));

        // 2. Business validation
        validateReviewRating(reviewRating);
        if (reviewContent != null && reviewContent.length() > 1000) {
            throw new IllegalArgumentException("Review content exceeds 1000 characters");
        }
        
        // 3. Check duplicate (direct entity check since OneToOne)
        if (order.getReview() != null) {
            throw new IllegalStateException(
                "ReviewServiceImpl.createReview - storeOrderId " + storeOrderId + " Review가 존재합니다.");
        }

        // 4. Build and persist (prePersist auto-sets timestamp)
        Review review = Review.builder()
                .storeOrder(order)
                .appUser(order.getAppUser())
                .store(order.getStore())
                .reviewRating(reviewRating)
                .reviewContent(reviewContent)
                .build();

        Review saved = reviewRepository.save(review);
        log.info("Review created successfully: reviewId={}, orderId={}", 
                saved.getReviewId(), storeOrderId);
        
        return reviewConverter.toDto(saved);
    }

    @Override
    public ReviewDTO getReview(Long reviewId) {
        if(reviewId == null || reviewId > 0){
            throw new IllegalArgumentException("ReviewServiceImpl - getReview - reviewId " + reviewId + "가 NULL입니다.");
        }
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException(
                    "ReviewServiceImpl.getReview - Review not found: " + reviewId));
        return reviewConverter.toDto(review);
    }

    @Override
    @Transactional
    public ReviewDTO updateReview(Long reviewId, ReviewDTO dto) {
        // NULL 확인
        if(reviewId == null || reviewId > 0){
            throw new IllegalArgumentException("ReviewServiceImpl - updateReview - reviewId " + reviewId + "가 NULL입니다.");
        }
        // 문제가 없다면 Review 받아옴.
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException(
                    "ReviewServiceImpl.updateReview - Review not found: " + reviewId));

        // 새로운 ReviewContent가 1000 char 이하인지 확인
        validateReviewRating(dto.getReviewRating());
        if (dto.getReviewContent() != null && dto.getReviewContent().length() > 1000) {
            throw new IllegalArgumentException("ReviewServiceImpl - updateReview - Param이 1000char 이상입니다.");
        }

        // 3. 수정 실시
        review.setReviewRating(dto.getReviewRating());
        review.setReviewContent(dto.getReviewContent());

        Review updated = reviewRepository.save(review);
        log.info("Review updated successfully: reviewId={}", reviewId);
        
        return reviewConverter.toDto(updated);
    }

    @Override
    @Transactional
    public void deleteReview(Long storeOrderId) {
        
        // Find review via order
        Review review = reviewRepository.findByStoreOrderOrderId(storeOrderId)
                .orElseThrow(() -> new IllegalArgumentException(
                    "ReviewServiceImpl.deleteReview - orderId " + storeOrderId + " Review가 없습니다."));
        // NULL 확인
        if(review == null){
            throw new IllegalArgumentException("ReviewServiceImpl - deleteReview - orderId " + storeOrderId + " Review가 없습니다.");
        }
        reviewRepository.delete(review);
        log.info("Review deleted successfully: reviewId={}, orderId={}", 
                review.getReviewId(), storeOrderId);
    }
    
    // Private validation helper
    private void validateReviewRating(BigDecimal rating) {
        if (rating == null || rating.compareTo(BigDecimal.valueOf(1.0)) < 0 
                || rating.compareTo(BigDecimal.valueOf(5.0)) > 0) {
            throw new IllegalArgumentException("Review rating이 1과 5 사이가 아닙니다.");
        }
    }
}
