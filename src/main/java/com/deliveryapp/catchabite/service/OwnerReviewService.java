package com.deliveryapp.catchabite.service;

import com.deliveryapp.catchabite.dto.OwnerReviewDTO;
import com.deliveryapp.catchabite.dto.PageResponseDTO;
import org.springframework.data.domain.Pageable;

public interface OwnerReviewService {

    PageResponseDTO<OwnerReviewDTO> listReviews(Long storeOwnerId, Long storeId, Pageable pageable);

    void writeReply(Long storeOwnerId, Long storeId, Long reviewId, String content);
}
