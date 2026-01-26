package com.deliveryapp.catchabite.repository;

import com.deliveryapp.catchabite.dto.OwnerPaymentDTO;
import com.deliveryapp.catchabite.entity.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface OwnerPaymentQueryRepository extends JpaRepository<Payment, Long> {

    @Query(
        value = """
            select new com.deliveryapp.catchabite.dto.OwnerPaymentDTO(
                p.paymentId,
                o.orderId,
                p.paymentMethod,
                p.paymentAmount,
                p.paymentStatus,
                p.paymentPaidAt
            )
            from Payment p
            join p.storeOrder o
            join o.store s
            join s.storeOwner so
            where s.storeId = :storeId
              and so.storeOwnerId = :storeOwnerId
              and (:status is null or p.paymentStatus = :status)
              and (:fromAt is null or p.paymentPaidAt >= :fromAt)
              and (:toAt is null or p.paymentPaidAt < :toAt)
            """,
        countQuery = """
            select count(p)
            from Payment p
            join p.storeOrder o
            join o.store s
            join s.storeOwner so
            where s.storeId = :storeId
              and so.storeOwnerId = :storeOwnerId
              and (:status is null or p.paymentStatus = :status)
              and (:fromAt is null or p.paymentPaidAt >= :fromAt)
              and (:toAt is null or p.paymentPaidAt < :toAt)
            """
    )
    Page<OwnerPaymentDTO> findOwnerPayments(
        @Param("storeOwnerId") Long storeOwnerId,
        @Param("storeId") Long storeId,
        @Param("status") String status,
        @Param("fromAt") LocalDateTime fromAt,
        @Param("toAt") LocalDateTime toAt,
        Pageable pageable
    );
}
