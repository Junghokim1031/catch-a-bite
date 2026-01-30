package com.deliveryapp.catchabite.service;

import java.math.BigDecimal;

import com.deliveryapp.catchabite.dto.PageResponseDTO;
import com.deliveryapp.catchabite.dto.ReviewDTO;

public interface ReviewService{
    public ReviewDTO createReview(Long storeOrderId, BigDecimal reviewRating, String reviewContent);
    public ReviewDTO getReview(Long reviewId);
    public ReviewDTO updateReview(Long reviewId, ReviewDTO dto);
    public void deleteReview(Long reviewId);
    public PageResponseDTO<ReviewDTO> getStoreReviews(Long storeId, int page, int size);
}
