package com.deliveryapp.catchabite.service;

import com.deliveryapp.catchabite.dto.OwnerPaymentDTO;
import com.deliveryapp.catchabite.repository.OwnerPaymentQueryRepository;
import com.deliveryapp.catchabite.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OwnerPaymentServiceImpl implements OwnerPaymentService {

    private final OwnerPaymentQueryRepository ownerPaymentQueryRepository;
    private final StoreRepository storeRepository;

    @Override
    public Page<OwnerPaymentDTO> listPayments(
        Long storeOwnerId,
        Long storeId,
        String status,
        LocalDate from,
        LocalDate to,
        Pageable pageable
    ) {

        // 소유권 선검증(메시지 명확히 + 빠른 실패)
        if (!storeRepository.existsByStoreIdAndStoreOwner_StoreOwnerId(storeId, storeOwnerId)) {
            throw new IllegalArgumentException("not your store");
        }

        LocalDateTime fromAt = (from == null) ? null : from.atStartOfDay();
        // to는 '해당 날짜 다음날 0시'로 미만(<) 비교 (기간 조회 정석)
        LocalDateTime toAt = (to == null) ? null : to.plusDays(1).atStartOfDay();

        return ownerPaymentQueryRepository.findOwnerPayments(
            storeOwnerId,
            storeId,
            status,
            fromAt,
            toAt,
            pageable
        );
    }
}
