// Project Name: catchabite
// File Name: catchabite/service/ReviewServiceImpl.java

package com.deliveryapp.catchabite.service;

import java.math.BigDecimal;
import java.util.stream.Collectors;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.deliveryapp.catchabite.entity.Review;
import com.deliveryapp.catchabite.entity.Store;
import com.deliveryapp.catchabite.entity.StoreOrder;
import com.deliveryapp.catchabite.dto.ReviewDTO;
import com.deliveryapp.catchabite.dto.PageResponseDTO;
import com.deliveryapp.catchabite.converter.ReviewConverter;
import com.deliveryapp.catchabite.repository.ReviewRepository;
import com.deliveryapp.catchabite.repository.StoreOrderRepository;
import com.deliveryapp.catchabite.repository.StoreRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewServiceImpl implements ReviewService {
    
    private final ReviewRepository reviewRepository;
    private final StoreOrderRepository storeOrderRepository;
    private final StoreRepository storeRepository;
    private final ReviewConverter reviewConverter;

    @Override
    @Transactional
    public ReviewDTO createReview(Long storeOrderId, BigDecimal reviewRating, String reviewContent) {
        if(storeOrderId == null || storeOrderId <= 0){
            throw new IllegalArgumentException("ReviewServiceImpl - createReview - storeOrderId " + storeOrderId + "가 NULL입니다.");
        }
        StoreOrder order = storeOrderRepository.findById(storeOrderId)
                .orElseThrow(() -> new IllegalArgumentException(
                    "ReviewServiceImpl - createReview - storeOrderId " + storeOrderId + "가 존재하지 않습니다."));

        validateReviewRating(reviewRating);
        if (reviewContent != null && reviewContent.length() > 1000) {
            throw new IllegalArgumentException("Review content exceeds 1000 characters");
        }
        
        if (order.getReview() != null) {
            throw new IllegalStateException(
                "ReviewServiceImpl.createReview - storeOrderId " + storeOrderId + " Review가 존재합니다.");
        }

        Review review = Review.builder()
                .storeOrder(order)
                .appUser(order.getAppUser())
                .store(order.getStore())
                .reviewRating(reviewRating)
                .reviewContent(reviewContent)
                .build();

        Review saved = reviewRepository.save(review);
        
        /* * [수정] 리뷰 저장 후 가게 평점 재계산 로직 호출
         * - 데이터 일관성을 위해 즉시 반영
         */
        updateStoreStatistics(saved.getStore().getStoreId());

        log.info("Review created successfully: reviewId={}, orderId={}", 
                saved.getReviewId(), storeOrderId);
        
        return reviewConverter.toDto(saved);
    }

    @Override
    public ReviewDTO getReview(Long reviewId) {
        if(reviewId == null || reviewId <= 0){
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
        if(reviewId == null || reviewId <= 0){
            throw new IllegalArgumentException("ReviewServiceImpl - updateReview - reviewId " + reviewId + "가 NULL입니다.");
        }
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException(
                    "ReviewServiceImpl.updateReview - Review not found: " + reviewId));

        validateReviewRating(dto.getReviewRating());
        if (dto.getReviewContent() != null && dto.getReviewContent().length() > 1000) {
            throw new IllegalArgumentException("ReviewServiceImpl - updateReview - Param이 1000char 이상입니다.");
        }

        review.setReviewRating(dto.getReviewRating());
        review.setReviewContent(dto.getReviewContent());

        Review updated = reviewRepository.save(review);

        /* * [수정] 리뷰 수정 후 가게 평점 재계산 로직 호출
         */
        updateStoreStatistics(updated.getStore().getStoreId());

        log.info("Review updated successfully: reviewId={}", reviewId);
        
        return reviewConverter.toDto(updated);
    }

    @Override
    @Transactional
    public void deleteReview(Long reviewId) { 
    
        if(reviewId == null || reviewId <= 0){
             throw new IllegalArgumentException("ReviewServiceImpl - deleteReview - 유효하지 않은 ID입니다.");
        }
        
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException(
                    "ReviewServiceImpl.deleteReview - reviewId " + reviewId + " Review가 없습니다."));

        Long storeId = review.getStore().getStoreId();

        reviewRepository.delete(review);
        
        /* * 리뷰 삭제 후 가게 평점 재계산 로직 호출
         * - delete 후 flush가 일어나야 AVG 쿼리에 반영됨.
         */
        reviewRepository.flush(); 
        updateStoreStatistics(storeId);

        log.info("Review deleted successfully: reviewId={}", reviewId);
    }
    
    @Override
    public PageResponseDTO<ReviewDTO> getStoreReviews(Long storeId, int page, int size) {
        if (storeId == null || storeId <= 0) {
             throw new IllegalArgumentException("Invalid Store ID");
        }

        // Sort by Created Date DESC
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "reviewCreatedAt"));
        Page<Review> reviewPage = reviewRepository.findByStore_StoreId(storeId, pageable);

        List<ReviewDTO> content = reviewPage.getContent().stream()
                .map(reviewConverter::toDto)
                .collect(Collectors.toList());

        // [Fix] Using Record Constructor
        return new PageResponseDTO<>(
                content,
                reviewPage.getNumber(),
                reviewPage.getSize(),
                reviewPage.getTotalElements(),
                reviewPage.getTotalPages(),
                reviewPage.hasNext()
        );
    }

    private void validateReviewRating(BigDecimal rating) {
        if (rating == null || rating.compareTo(BigDecimal.valueOf(1.0)) < 0 
                || rating.compareTo(BigDecimal.valueOf(5.0)) > 0) {
            throw new IllegalArgumentException("Review rating이 1과 5 사이가 아닙니다.");
        }
    }
    /*
     * ============================================================
     * 가게 평점 재계산 Helper Method
     * - ReviewRepository의 AVG 쿼리를 사용하여 Store 엔티티를 업데이트합니다.
     * ============================================================
     */
    private void updateStoreStatistics(Long storeId) {
        if (storeId == null) return;

        // 1. DB에서 현재 평균 평점 계산 (null이면 0.0 처리)
        Double avgRating = reviewRepository.getAverageRating(storeId);
        if (avgRating == null) {
            avgRating = 0.0;
        }

        // 2. 소수점 1자리 반올림 (선택 사항, 필요 시 로직 추가)
        // avgRating = Math.round(avgRating * 10.0) / 10.0;

        // 3. Store 엔티티 조회 및 업데이트
        // 영속성 컨텍스트 내에서 관리되도록 findById 사용
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new IllegalStateException("Store not found while updating rating: " + storeId));
        
        store.updateStoreRating(avgRating);
        
        // @Transactional 안이므로 Dirty Checking에 의해 자동 update 쿼리 발생
    }
}