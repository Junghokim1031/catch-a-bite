package com.deliveryapp.catchabite.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/*
 * Review(리뷰)에 대한 사업자(사장) 답글 엔티티입니다.
 * - 1 review : 1 reply (review_id unique)
 */
@Entity
@Table(name = "REVIEW_REPLY")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewReply {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "REVIEW_REPLY_ID")
    private Long reviewReplyId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "REVIEW_ID", nullable = false, unique = true)
    private Review review;

    @Column(name = "REVIEW_REPLY_CONTENT", length = 1000, nullable = false)
    private String reviewReplyContent;

    @Column(name = "REVIEW_REPLY_CREATED_AT", nullable = false)
    private LocalDateTime reviewReplyCreatedAt;

    @Column(name = "REVIEW_REPLY_UPDATED_AT", nullable = false)
    private LocalDateTime reviewReplyUpdatedAt;

    public void changeContent(String content) {
        this.reviewReplyContent = content;
        this.reviewReplyUpdatedAt = LocalDateTime.now();
    }

    @PrePersist
    private void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        if (reviewReplyCreatedAt == null) {
            reviewReplyCreatedAt = now;
        }
        if (reviewReplyUpdatedAt == null) {
            reviewReplyUpdatedAt = now;
        }
    }
}
