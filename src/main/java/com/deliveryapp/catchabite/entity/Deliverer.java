package com.deliveryapp.catchabite.entity;

import java.time.LocalDateTime;

import com.deliveryapp.catchabite.domain.enumtype.DelivererVehicleType;
import com.deliveryapp.catchabite.domain.enumtype.YesNo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "deliverer")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Deliverer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "deliverer_id")
    private Long delivererId;


    @Column(name = "deliverer_email", nullable = false, unique = true, length = 255)
    private String delivererEmail; // 로그인 이메일

    @Column(name = "deliverer_password", nullable = false, length = 255)
    private String delivererPassword; // 비밀번호


    // 오토바이/자전거/차량/walking
    @Enumerated(EnumType.STRING)
    @Column(name = "deliverer_vehicle_type", length = 50, nullable = false)
    private DelivererVehicleType delivererVehicleType;

    @Column(name = "deliverer_license_number", length = 50, unique = true)
    private String delivererLicenseNumber;

    @Column(name = "deliverer_vehicle_number", length = 50)
    private String delivererVehicleNumber;

    // 배달 가능 상태
    // Y: 가능 / N: 불가능
    @Enumerated(EnumType.STRING)
    @Column(name = "deliverer_status", length = 1, nullable = true)
    private YesNo delivererStatus;

    // 마지막 로그인
    @Column(name = "deliverer_last_login_date")
    private LocalDateTime delivererLastLoginDate;

    // 본인인증 여부 (Y/N)
    @Enumerated(EnumType.STRING)
    @Column(name = "deliverer_verified", length = 1, nullable = true)
    private YesNo delivererVerified;

    @Column(name = "deliverer_created_date")
    private LocalDateTime delivererCreatedDate; // 생성일

    @PrePersist
    void prePersist() {
        // MariaDB에서 deliverer_status의 기본값을 'Y'로 초기화
        if (delivererStatus == null) delivererStatus = YesNo.Y;     // Oracle DEFAULT 'Y'
        // MariaDB에서 deliverer_verified의 기본값을 'N'로 초기화
        if (delivererVerified == null) delivererVerified = YesNo.N; // Oracle DEFAULT 'N'
        if (delivererCreatedDate == null) delivererCreatedDate = LocalDateTime.now(); // 생성일 자동
    }

}