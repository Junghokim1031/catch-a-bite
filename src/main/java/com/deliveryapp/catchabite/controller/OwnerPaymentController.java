package com.deliveryapp.catchabite.controller;

import com.deliveryapp.catchabite.dto.OwnerPaymentDTO;
import com.deliveryapp.catchabite.security.OwnerContext;
import com.deliveryapp.catchabite.service.OwnerPaymentService;
import com.deliveryapp.catchabite.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/owner/stores/{storeId}/payments")
public class OwnerPaymentController {

    private final OwnerPaymentService ownerPaymentService;
    private final OwnerContext ownerContext;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<OwnerPaymentDTO>>> list(
        Principal principal,
        @PathVariable Long storeId,
        @RequestParam(required = false) String status,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
        @PageableDefault(size = 20) Pageable pageable
    ) {
        Long storeOwnerId = ownerContext.requireStoreOwnerId(principal);
        return ResponseEntity.ok(
            ApiResponse.ok(ownerPaymentService.listPayments(storeOwnerId, storeId, status, from, to, pageable))
        );
    }
}
