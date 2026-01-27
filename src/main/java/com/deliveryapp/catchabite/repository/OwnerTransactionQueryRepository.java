package com.deliveryapp.catchabite.repository;

import com.deliveryapp.catchabite.domain.enumtype.TransactionType;
import com.deliveryapp.catchabite.dto.OwnerTransactionDTO;
import com.deliveryapp.catchabite.transaction.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface OwnerTransactionQueryRepository extends JpaRepository<Transaction, Long> {

    @Query(
        value = """
            select new com.deliveryapp.catchabite.dto.OwnerTransactionDTO(
                t.transactionId,
                t.transactionType,
                o.orderId,
                t.amount,
                t.currency,
                t.transactionStatus,
                t.portonePaymentId,
                t.portoneTransferId,
                t.createdAt,
                t.completedAt
            )
            from Transaction t, StoreOrder o
            where t.relatedEntityType = 'ORDER'
              and t.relatedEntityId = o.orderId
              and o.store.storeId = :storeId
              and o.store.storeOwner.storeOwnerId = :storeOwnerId
              and (:type is null or t.transactionType = :type)
              and (:status is null or t.transactionStatus = :status)
              and (:fromAt is null or t.createdAt >= :fromAt)
              and (:toAt is null or t.createdAt < :toAt)
            """,
        countQuery = """
            select count(t)
            from Transaction t, StoreOrder o
            where t.relatedEntityType = 'ORDER'
              and t.relatedEntityId = o.orderId
              and o.store.storeId = :storeId
              and o.store.storeOwner.storeOwnerId = :storeOwnerId
              and (:type is null or t.transactionType = :type)
              and (:status is null or t.transactionStatus = :status)
              and (:fromAt is null or t.createdAt >= :fromAt)
              and (:toAt is null or t.createdAt < :toAt)
            """
    )
    Page<OwnerTransactionDTO> findOwnerTransactions(
        @Param("storeOwnerId") Long storeOwnerId,
        @Param("storeId") Long storeId,
        @Param("type") TransactionType type,
        @Param("status") String status,
        @Param("fromAt") LocalDateTime fromAt,
        @Param("toAt") LocalDateTime toAt,
        Pageable pageable
    );
}
