package com.deliveryapp.catchabite.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "REVIEW")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString()
public class Review {

    @Id
    @Column(name = "REVIEW_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reviewId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="ORDER_ID", nullable = false)
    private StoreOrder order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="APP_USER_ID", nullable = false)
    private AppUser appUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="STORE_ID", nullable = false)
    private Store store;

    @Column(name="REVIEW_RATING", nullable = false)
    private Double reviewRating;

    @Column(name="REVIEW_CONTENT", length = 1000)
    private String reviewContent;

    @Column(name="REVIEW_CREATED_AT", nullable = false)
    private LocalDateTime reviewCreatedAt;

}