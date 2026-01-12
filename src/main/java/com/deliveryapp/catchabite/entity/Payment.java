package com.deliveryapp.catchabite.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;


@Entity
@Table(name = "PAYMENT")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Payment {
    @Id
    @Column(name = "PAYMENT_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentId;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name="ORDER_ID", nullable = false)
    private StoreOrder order;

    @Column(name = "PAYMENT_METHOD", nullable = false, length = 100)
    private String paymentMethod;

    @Column(name = "PAYMENT_AMOUNT", nullable = false)
    private Integer paymentAmount;

    @Column(name = "PAYMENT_STATUS", nullable = false, length = 50)
    private String paymentStatus;

    @Column(name = "PAYMENT_PAID_AT", nullable = false)
    private LocalDateTime paymentPaidAt;
}