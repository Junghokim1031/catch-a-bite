package com.deliveryapp.catchabite.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class OwnerReviewDTO {

    private Long reviewId;
    private Long orderId;
    private Long appUserId;
    private Long storeId;

    private BigDecimal reviewRating;
    private String reviewContent;
    private LocalDateTime reviewCreatedAt;

    // reply
    private Long reviewReplyId;
    private String reviewReplyContent;
    private LocalDateTime reviewReplyCreatedAt;
    private LocalDateTime reviewReplyUpdatedAt;
}
