package com.deliveryapp.catchabite.payment.repository;

import com.deliveryapp.catchabite.entity.Payment;
import com.deliveryapp.catchabite.entity.StoreOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/**
 * PaymentRepository: Payment 엔티티의 데이터 접근 계층
 * 
 * Description: Spring Data JPA를 사용하여 Payment 테이블에 CRUD 작업을 수행합니다.
 * StoreOrder 관계를 통해 주문별 결제 정보를 조회합니다.
 * 
 * Required Variables/Parameters: (Repository 인터페이스)
 * 
 * Output/Data Flow:
 * - Called by PaymentService and PaymentVerificationService
 * - Queries PAYMENT table with relationships to STOREORDER
 * 
 * Dependencies: Spring Data JPA, Payment entity
 */

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    
    /**
     * 주문(StoreOrder)의 결제 정보 조회
     * 
     * @param order 주문 객체 (StoreOrder)
     * @return Optional<Payment> - 해당 주문의 결제 정보
     * 
     * SQL: SELECT * FROM PAYMENT WHERE ORDERID = ?
     */
    Optional<Payment> findByStoreOrder(StoreOrder order);
    
    /**
     * 상태별 결제 조회 (선택사항, 향후 사용)
     * 
     * @param status 결제 상태
     * @return 해당 상태의 모든 결제 리스트
     * 
     * SQL: SELECT * FROM PAYMENT WHERE PAYMENTSTATUS = ?
     */
    // List<Payment> findByPaymentStatus(String status);
}
