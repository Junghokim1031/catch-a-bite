package com.deliveryapp.catchabite.service;

import com.deliveryapp.catchabite.domain.enumtype.TransactionType;
import com.deliveryapp.catchabite.dto.OwnerTransactionDTO;
import com.deliveryapp.catchabite.repository.OwnerTransactionQueryRepository;
import com.deliveryapp.catchabite.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OwnerTransactionServiceImpl implements OwnerTransactionService {

    private final OwnerTransactionQueryRepository ownerTransactionQueryRepository;
    private final StoreRepository storeRepository;

    @Override
    public Page<OwnerTransactionDTO> listTransactions(
        Long storeOwnerId,
        Long storeId,
        TransactionType type,
        String status,
        LocalDate from,
        LocalDate to,
        Pageable pageable
    ) {
        // OwnerPaymentServiceImpl과 동일한 소유권 체크 패턴 (src.zip에 해당 메서드 존재 확인)
        if (!storeRepository.existsByStoreIdAndStoreOwner_StoreOwnerId(storeId, storeOwnerId)) {
            throw new IllegalArgumentException("내 매장이 아닙니다. storeId=" + storeId);
        }

        TransactionType effectiveType = (type == null) ? TransactionType.STORE_PAYOUT : type;

        LocalDateTime fromAt = (from == null) ? null : from.atStartOfDay();
        LocalDateTime toAt = (to == null) ? null : to.plusDays(1).atStartOfDay();

        return ownerTransactionQueryRepository.findOwnerTransactions(
            storeOwnerId,
            storeId,
            effectiveType,
            status,
            fromAt,
            toAt,
            pageable
        );
    }
}
