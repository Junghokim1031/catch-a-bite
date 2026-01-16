package com.deliveryapp.catchabite.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/*
 * 이 클래스는 일부러 @Setter를 작성하지 않았습니다. 
 * @Builder만 사용함으로 null이 발생하는 것을 방지하고자 합니다.
 */
@Entity
@Table(name = "review")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Review {

    @Id
    @Column(name = "review_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reviewId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="order_id", nullable = false)
    private StoreOrder storeOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="app_user_id", nullable = false)
    private AppUser appUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="store_id", nullable = false)
    private Store store;

    @Column(name="review_rating", nullable = false)
    private BigDecimal reviewRating;

    @Column(name="review_content", length = 1000)
    private String reviewContent;

    @Column(name="review_created_at", nullable = false)
    private LocalDateTime reviewCreatedAt;

    /**
     * 주문 최초 저장(INSERT) 직전에 reviewCreatedAt를 자동 세팅합니다.
     * 이미 reviewCreatedAt이 지정된 경우(외부 입력/특수 케이스)에는 덮어쓰이지 않습니다.
     */
    @PrePersist
    private void prePersist() {
        if (reviewCreatedAt == null) 
        {
            reviewCreatedAt = LocalDateTime.now();
        }
    }

    public void setReviewRating(BigDecimal reviewRating){
        this.reviewRating = reviewRating;
    }

    public void setReviewContent(String reviewContent){
        this.reviewContent = reviewContent;
    }
}