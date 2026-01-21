package com.deliveryapp.catchabite.service;

import com.deliveryapp.catchabite.dto.OwnerPaymentDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface OwnerPaymentService {
    Page<OwnerPaymentDTO> listPayments(Long storeOwnerId, Long storeId, String status, LocalDate from, LocalDate to, Pageable pageable);
}
