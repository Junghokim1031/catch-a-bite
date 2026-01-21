package com.deliveryapp.catchabite.controller;

import com.deliveryapp.catchabite.common.response.ApiResponse;
import com.deliveryapp.catchabite.domain.enumtype.TransactionType;
import com.deliveryapp.catchabite.dto.OwnerTransactionDTO;
import com.deliveryapp.catchabite.security.OwnerContext;
import com.deliveryapp.catchabite.service.OwnerTransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/owner/stores/{storeId}/transactions")
public class OwnerTransactionController {

    private final OwnerTransactionService ownerTransactionService;
    private final OwnerContext ownerContext;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<OwnerTransactionDTO>>> list(
        Principal principal,
        @PathVariable Long storeId,
        @RequestParam(required = false) TransactionType type,
        @RequestParam(required = false) String status,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
        @PageableDefault(size = 20) Pageable pageable
    ) {
        Long storeOwnerId = ownerContext.requireStoreOwnerId(principal);
        return ResponseEntity.ok(
            ApiResponse.ok(ownerTransactionService.listTransactions(storeOwnerId, storeId, type, status, from, to, pageable))
        );
    }
}
