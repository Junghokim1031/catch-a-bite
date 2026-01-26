package com.deliveryapp.catchabite.service;

import com.deliveryapp.catchabite.dto.OwnerReviewDTO;
import com.deliveryapp.catchabite.dto.PageResponseDTO;
import com.deliveryapp.catchabite.entity.Review;
import com.deliveryapp.catchabite.entity.ReviewReply;
import com.deliveryapp.catchabite.repository.ReviewReplyRepository;
import com.deliveryapp.catchabite.repository.ReviewRepository;
import com.deliveryapp.catchabite.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class OwnerReviewServiceImpl implements OwnerReviewService {

    private final StoreRepository storeRepository;
    private final ReviewRepository reviewRepository;
    private final ReviewReplyRepository reviewReplyRepository;

    @Override
    @Transactional(readOnly = true)
    public PageResponseDTO<OwnerReviewDTO> listReviews(Long storeOwnerId, Long storeId, Pageable pageable) {

        validateOwnedStore(storeOwnerId, storeId);

        Page<Review> page = reviewRepository.findByStore_StoreId(storeId, pageable);

        Collection<Long> reviewIds = page.getContent().stream()
                .map(Review::getReviewId)
                .toList();

        Map<Long, ReviewReply> replyMap = reviewIds.isEmpty()
                ? Map.of()
                : reviewReplyRepository.findByReview_ReviewIdIn(reviewIds).stream()
                .collect(Collectors.toMap(rr -> rr.getReview().getReviewId(), Function.identity()));

        Page<OwnerReviewDTO> dtoPage = page.map(r -> {
            ReviewReply rr = replyMap.get(r.getReviewId());
            return OwnerReviewDTO.builder()
                    .reviewId(r.getReviewId())
                    .orderId(r.getStoreOrder() == null ? null : r.getStoreOrder().getOrderId())
                    .appUserId(r.getAppUser() == null ? null : r.getAppUser().getAppUserId())
                    .storeId(r.getStore() == null ? null : r.getStore().getStoreId())
                    .reviewRating(r.getReviewRating())
                    .reviewContent(r.getReviewContent())
                    .reviewCreatedAt(r.getReviewCreatedAt())
                    .reviewReplyId(rr == null ? null : rr.getReviewReplyId())
                    .reviewReplyContent(rr == null ? null : rr.getReviewReplyContent())
                    .reviewReplyCreatedAt(rr == null ? null : rr.getReviewReplyCreatedAt())
                    .reviewReplyUpdatedAt(rr == null ? null : rr.getReviewReplyUpdatedAt())
                    .build();
        });

        return PageResponseDTO.from(dtoPage);
    }

    @Override
    public void writeReply(Long storeOwnerId, Long storeId, Long reviewId, String content) {

        validateOwnedStore(storeOwnerId, storeId);

        Review review = reviewRepository.findByReviewIdAndStore_StoreId(reviewId, storeId)
                .orElseThrow(() -> new IllegalArgumentException("review not found"));

        ReviewReply existing = reviewReplyRepository.findByReview_ReviewId(reviewId).orElse(null);
        if (existing != null) {
            throw new IllegalArgumentException("reply already exists");
        }

        ReviewReply reply = ReviewReply.builder()
                .review(review)
                .reviewReplyContent(content)
                .build();

        reviewReplyRepository.save(reply);
    }

    private void validateOwnedStore(Long storeOwnerId, Long storeId) {
        boolean owned = storeRepository.existsByStoreIdAndStoreOwner_StoreOwnerId(storeId, storeOwnerId);
        if (!owned) {
            throw new IllegalArgumentException("not your store");
        }
    }
}
