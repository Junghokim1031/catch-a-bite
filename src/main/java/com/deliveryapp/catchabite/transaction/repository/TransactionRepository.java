package com.deliveryapp.catchabite.transaction.repository;

import com.deliveryapp.catchabite.transaction.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

/**
 * TransactionRepository: Transaction 엔티티의 데이터 접근 계층
 * 
 * Description: Spring Data JPA를 사용하여 Transaction 테이블에 CRUD 작업을 수행합니다.
 * 모든 결제, 정산 거래 기록을 저장하고 조회합니다.
 * 
 * Required Variables/Parameters: (Repository 인터페이스)
 * 
 * Output/Data Flow:
 * - Called by TransactionService
 * - Stores records from PaymentVerificationService
 * - Queries for transaction history and audit trails
 * 
 * Dependencies: Spring Data JPA, Transaction entity
 */

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    
    /**
     * 특정 주문의 거래 내역 조회
     * 
     * @param relatedEntityId 관련 엔티티 ID (주문 ID 등)
     * @return 해당 ID의 모든 거래 리스트
     * 
     * SQL: SELECT * FROM TRANSACTION WHERE RELATEDENTITYID = ?
     */
    List<Transaction> findByRelatedEntityId(Long relatedEntityId);
    
    /**
     * PortOne 결제 ID로 거래 조회 (중복 방지)
     * 
     * @param portonePaymentId PortOne의 imp_uid
     * @return 해당하는 거래 정보
     * 
     * SQL: SELECT * FROM TRANSACTION WHERE PORTONEPAYMENTID = ?
     */
    Optional<Transaction> findByPortonePaymentId(String portonePaymentId);
}
