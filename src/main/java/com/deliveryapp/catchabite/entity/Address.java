package com.deliveryapp.catchabite.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * 사용자 배송지 엔티티
 * APP_USER와 연관된 주소 정보
 */
@Entity
@Table(name = "ADDRESS")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Address {

    // 주소 PK
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ADDRESS_ID")
    private Long addressId;

    // 주소 소유 사용자 (N:1)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "APP_USER_ID", nullable = false)
    private AppUser appUser;

    // 상세 주소
    @Column(name = "ADDRESS_DETAIL", nullable = false, length = 255)
    private String addressDetail;

    // 주소 별칭
    @Column(name = "ADDRESS_NICKNAME", length = 50)
    private String addressNickname;

    // 출입 방법 안내
    @Column(name = "ADDRESS_ENTRANCE_METHOD", length = 100)
    private String addressEntranceMethod;

    // 기본 배송지 여부 (Y/N)
    @Column(name = "ADDRESS_IS_DEFAULT", length = 1)
    private String addressIsDefault;

    // 주소 생성일
    @Column(name = "ADDRESS_CREATED_DATE")
    private LocalDate addressCreatedDate;

    // 주소 사용 여부 (Y/N)
    @Column(name = "ADDRESS_VISIBLE", length = 1)
    private String addressVisible;

    // 해당 주소로 주문된 주문 목록
    @OneToMany(mappedBy = "address", cascade = CascadeType.ALL)
    @Builder.Default
    private List<StoreOrder> orders = new ArrayList<>();
}
