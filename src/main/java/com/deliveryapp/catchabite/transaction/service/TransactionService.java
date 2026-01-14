package com.deliveryapp.catchabite.transaction.service;

import com.deliveryapp.catchabite.transaction.entity.Transaction;
import com.deliveryapp.catchabite.transaction.repository.TransactionRepository;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

/**
 * TransactionService: 거래 기록 관리 서비스
 * 
 * Description: 모든 결제, 정산 거래를 기록하고 조회합니다.
 * 감사 추적(audit trail)을 위해 모든 거래를 저장합니다.
 * 
 * Required Variables/Parameters:
 * - transactionRepository (TransactionRepository): 거래 저장/조회
 * 
 * Output/Data Flow:
 * - Receives Transaction from PaymentVerificationService
 * - Saves to TRANSACTION table
 * - Provides transaction history for queries
 * 
 * Dependencies: TransactionRepository, Slf4j
 */

@Log4j2
@Service
public class TransactionService {
    
    private final TransactionRepository transactionRepository;
    
    /**
     * 생성자 - Dependency Injection
     * 
     * @param transactionRepository 거래 저장/조회 Repository
     */
    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }
    
    /**
     * 거래 기록 저장
     * 
     * @param transaction 저장할 거래 정보
     * @return 저장된 Transaction 객체
     */
    @Transactional
    public Transaction saveTransaction(Transaction transaction) {
        try {
            log.info("Saving transaction. type: {}, related_entity_id: {}", 
                    transaction.getTransactionType(), 
                    transaction.getRelatedEntityId());
            
            Transaction saved = transactionRepository.save(transaction);
            
            log.info("Transaction saved successfully. transaction_id: {}", 
                    saved.getTransactionId());
            
            return saved;
        } catch (Exception e) {
            log.error("Error saving transaction", e);
            throw e;
        }
    }
    
    /**
     * 거래 조회 (ID로)
     * 
     * @param transactionId 거래 ID
     * @return 거래 정보
     */
    public Optional<Transaction> getTransactionById(Long transactionId) {
        return transactionRepository.findById(transactionId);
    }
    
    /**
     * 특정 엔티티의 모든 거래 조회
     * 
     * @param relatedEntityId 관련 엔티티 ID (주문 ID 등)
     * @return 거래 리스트
     */
    public List<Transaction> getTransactionsByRelatedEntityId(Long relatedEntityId) {
        return transactionRepository.findByRelatedEntityId(relatedEntityId);
    }
    
    /**
     * PortOne 결제 ID로 거래 조회 (중복 결제 방지)
     * 
     * @param portonePaymentId PortOne imp_uid
     * @return 거래 정보
     */
    public Optional<Transaction> getTransactionByPortonePaymentId(
            String portonePaymentId) {
        return transactionRepository.findByPortonePaymentId(portonePaymentId);
    }
}
