package com.deliveryapp.catchabite.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "deliverer_fee_rule")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DelivererFeeRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rule_id")
    private Long ruleId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "deliverer_id", nullable = false)
    private Deliverer deliverer;

    @Column(name = "min_m", nullable = false)
    private Integer minM;

    @Column(name = "max_m")
    private Integer maxM; // null이면 무한대

    @Column(name = "base_fee", nullable = false)
    private Long baseFee;

    @Column(name = "per_km_fee", nullable = false)
    private Long perKmFee;

    @Column(name = "active_yn", nullable = false, length = 1)
    private String activeYn; // "Y"/"N"

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void prePersist() {
        if (activeYn == null) activeYn = "Y";
        if (createdAt == null) createdAt = LocalDateTime.now();
    }
}
