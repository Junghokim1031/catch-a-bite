package com.deliveryapp.catchabite.converter;

import org.springframework.stereotype.Component;
import com.deliveryapp.catchabite.dto.ReviewDTO;
import com.deliveryapp.catchabite.entity.Review;
import com.deliveryapp.catchabite.entity.StoreOrder;
import com.deliveryapp.catchabite.entity.AppUser;
import com.deliveryapp.catchabite.entity.Store;
import com.deliveryapp.catchabite.entity.ReviewReply;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ReviewConverter {
    
    public ReviewDTO toDto(Review entity) {
        if (entity == null) return null;
        
        // AppUser가 Lazy Loading일 수 있으므로 null 체크
        String nickname = (entity.getAppUser() != null) ? entity.getAppUser().getAppUserNickname() : "알 수 없음";
        String replyContent = null;
        java.time.LocalDateTime replyDate = null;

        if (entity.getReviewReply() != null) {
            ReviewReply reply = entity.getReviewReply();
            replyContent = reply.getReviewReplyContent();
            replyDate = reply.getReviewReplyCreatedAt();
        }
        
        return ReviewDTO.builder()
                .reviewId(entity.getReviewId())
                .storeOrderId(entity.getStoreOrder() != null ? entity.getStoreOrder().getOrderId() : null)
                .appUserId(entity.getAppUser() != null ? entity.getAppUser().getAppUserId() : null)
                .storeId(entity.getStore() != null ? entity.getStore().getStoreId() : null)
                .authorNickname(nickname)
                .reviewRating(entity.getReviewRating())
                .reviewContent(entity.getReviewContent())
                .reviewCreatedAt(entity.getReviewCreatedAt())
                .ownerReplyContent(replyContent)
                .ownerReplyCreatedAt(replyDate)
                .build();
    }
    
    public Review toEntity(ReviewDTO dto, StoreOrder storeOrder, 
                          AppUser appUser, Store store) {
        if (dto == null) return null;
        
        return Review.builder()
                .reviewId(dto.getReviewId())
                .storeOrder(storeOrder)
                .appUser(appUser)
                .store(store)
                .reviewRating(dto.getReviewRating())
                .reviewContent(dto.getReviewContent())
                .reviewCreatedAt(dto.getReviewCreatedAt())
                .build();
    }
}
