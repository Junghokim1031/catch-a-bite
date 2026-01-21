package com.deliveryapp.catchabite.service;

import com.deliveryapp.catchabite.domain.enumtype.TransactionType;
import com.deliveryapp.catchabite.dto.OwnerTransactionDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface OwnerTransactionService {

    Page<OwnerTransactionDTO> listTransactions(
        Long storeOwnerId,
        Long storeId,
        TransactionType type,   // null이면 STORE_PAYOUT 기본 적용
        String status,
        LocalDate from,
        LocalDate to,
        Pageable pageable
    );
}
