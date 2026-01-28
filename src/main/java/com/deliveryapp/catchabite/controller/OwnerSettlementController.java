package com.deliveryapp.catchabite.controller;

import com.deliveryapp.catchabite.common.response.ApiResponse;
import com.deliveryapp.catchabite.domain.enumtype.OwnerSettlementItemStatus;
import com.deliveryapp.catchabite.domain.enumtype.OwnerSettlementStatus;
import com.deliveryapp.catchabite.dto.OwnerSettlementItemDTO;
import com.deliveryapp.catchabite.dto.OwnerSettlementSummaryDTO;
import com.deliveryapp.catchabite.security.OwnerContext;
import com.deliveryapp.catchabite.service.OwnerSettlementService;
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
@RequestMapping("/api/v1/owner")
public class OwnerSettlementController {

	private final OwnerSettlementService ownerSettlementService;
	private final OwnerContext ownerContext;

	/**
	 * 기간별 정산 목록
	 */
	@GetMapping("/settlements")
	public ResponseEntity<ApiResponse<Page<OwnerSettlementSummaryDTO>>> listSettlements(
			Principal principal,
			@RequestParam(required = false) OwnerSettlementStatus status,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
			@PageableDefault(size = 20) Pageable pageable
	) {
		Long storeOwnerId = ownerContext.requireStoreOwnerId(principal);
		return ResponseEntity.ok(ApiResponse.ok(
				ownerSettlementService.listSettlements(storeOwnerId, status, from, to, pageable)
		));
	}

	/**
	 * 주문별 정산(라인) 목록
	 * - storeId/ownerSettlementId 없이도 조회 가능
	 */
	@GetMapping("/settlement-items")
	public ResponseEntity<ApiResponse<Page<OwnerSettlementItemDTO>>> listSettlementItems(
			Principal principal,
			@RequestParam(required = false) Long storeId,
			@RequestParam(required = false) Long ownerSettlementId,
			@RequestParam(required = false) OwnerSettlementItemStatus status,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
			@PageableDefault(size = 20) Pageable pageable
	) {
		Long storeOwnerId = ownerContext.requireStoreOwnerId(principal);
		return ResponseEntity.ok(ApiResponse.ok(
				ownerSettlementService.listSettlementItems(storeOwnerId, storeId, ownerSettlementId, status, from, to, pageable)
		));
	}

	/**
	 * 기간 정산 생성
	 */
	@PostMapping("/settlements")
	public ResponseEntity<ApiResponse<Long>> createSettlement(
			Principal principal,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodStart,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodEnd
	) {
		Long storeOwnerId = ownerContext.requireStoreOwnerId(principal);
		Long settlementId = ownerSettlementService.createSettlement(storeOwnerId, periodStart, periodEnd);
		return ResponseEntity.ok(ApiResponse.ok(settlementId));
	}

	/**
	 * 지급 완료 처리(포트폴리오/테스트용)
	 */
	@PostMapping("/settlements/{ownerSettlementId}/payout")
	public ResponseEntity<ApiResponse<Void>> payoutSettlement(
			Principal principal,
			@PathVariable Long ownerSettlementId,
			@RequestParam(required = false) String portoneTransferId
	) {
		Long storeOwnerId = ownerContext.requireStoreOwnerId(principal);
		ownerSettlementService.payoutSettlement(storeOwnerId, ownerSettlementId, portoneTransferId);
		return ResponseEntity.ok(ApiResponse.ok(null));
	}
}
