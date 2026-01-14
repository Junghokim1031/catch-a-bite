package com.deliveryapp.catchabite.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

/**
 * 알림 엔티티
 * 사용자에게 전달되는 알림 정보
 */
@Entity
@Table(name = "NOTIFICATION")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

    // 알림 PK
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "NOTIFICATION_ID")
    private Long notificationId;

    // 알림 대상 사용자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "APP_USER_ID", nullable = false)
    private AppUser appUser;

    // 알림 메시지
    @Column(name = "NOTIFICATION_MESSAGE", nullable = false, length = 500)
    private String notificationMessage;

    // 읽음 여부 (Y/N)
    @Column(name = "NOTIFICATION_IS_READ", length = 1)
    private String notificationIsRead;

    // 알림 생성일
    @Column(name = "NOTIFICATION_CREATED_AT")
    private LocalDate notificationCreatedAt;
}
