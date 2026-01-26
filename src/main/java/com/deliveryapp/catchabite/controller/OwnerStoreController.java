package com.deliveryapp.catchabite.controller;

import com.deliveryapp.catchabite.common.response.ApiResponse;
import com.deliveryapp.catchabite.dto.StoreDTO;
import com.deliveryapp.catchabite.dto.StoreDeliveryConditionPatchRequestDTO;
import com.deliveryapp.catchabite.dto.StorePatchRequestDTO;
import com.deliveryapp.catchabite.dto.StoreStatusChangeRequestDTO;
import com.deliveryapp.catchabite.dto.StoreSummaryDTO;
import com.deliveryapp.catchabite.security.OwnerContext;
import com.deliveryapp.catchabite.service.StoreService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/owner/stores")
public class OwnerStoreController {

	private final StoreService storeService;
	private final OwnerContext ownerContext;

	// 내 매장 목록(요약) 조회
	@GetMapping
	public ResponseEntity<ApiResponse<List<StoreSummaryDTO>>> getMyStores(Principal principal) {

		Long storeOwnerId = ownerContext.requireStoreOwnerId(principal);

		List<StoreSummaryDTO> stores = storeService.getMyStores(storeOwnerId);
		return ResponseEntity.ok(ApiResponse.ok(stores));
	}

	// 매장 정보 조회
	@GetMapping("/{storeId}")
	public ResponseEntity<ApiResponse<StoreDTO>> getStoreInfo(Principal principal,
													@PathVariable Long storeId) {

		Long storeOwnerId = ownerContext.requireStoreOwnerId(principal);

		StoreDTO store = storeService.getStoreInfo(storeOwnerId, storeId);
		return ResponseEntity.ok(ApiResponse.ok(store));
	}

	// 매장 등록
	@PostMapping
	public ResponseEntity<ApiResponse<StoreDTO>> createStore(Principal principal,
														@RequestBody @Valid StoreDTO dto) {

		Long storeOwnerId = ownerContext.requireStoreOwnerId(principal);

		StoreDTO result = storeService.createStore(storeOwnerId, dto);
		return ResponseEntity.ok(ApiResponse.ok(result));
	}

	// 매장 기본 정보 수정
	@PutMapping("/{storeId}")
	public ResponseEntity<ApiResponse<StoreDTO>> updateStore(Principal principal,
														@PathVariable Long storeId,
														@RequestBody @Valid StoreDTO dto) {

		Long storeOwnerId = ownerContext.requireStoreOwnerId(principal);

		StoreDTO result = storeService.updateStoreBasicInfo(storeOwnerId, storeId, dto);
		return ResponseEntity.ok(ApiResponse.ok(result));
	}

	// 매장 기본 정보 부분 수정
	@PatchMapping("/{storeId}")
	public ResponseEntity<ApiResponse<StoreDTO>> patchStore(Principal principal,
											@PathVariable Long storeId,
											@RequestBody @Valid StorePatchRequestDTO dto) {

		Long storeOwnerId = ownerContext.requireStoreOwnerId(principal);

		StoreDTO result = storeService.patchStoreBasicInfo(storeOwnerId, storeId, dto);
		return ResponseEntity.ok(ApiResponse.ok(result));
	}

	// 배달조건 부분 수정 (P1)
	@PatchMapping("/{storeId}/delivery-condition")
	public ResponseEntity<ApiResponse<StoreDTO>> patchStoreDeliveryCondition(Principal principal,
										@PathVariable Long storeId,
										@RequestBody @Valid StoreDeliveryConditionPatchRequestDTO dto) {

		Long storeOwnerId = ownerContext.requireStoreOwnerId(principal);

		StoreDTO result = storeService.patchStoreDeliveryCondition(storeOwnerId, storeId, dto);
		return ResponseEntity.ok(ApiResponse.ok(result));
	}

	// 영업 상태 변경
	@PatchMapping("/{storeId}/status")
	public ResponseEntity<ApiResponse<Object>> changeStoreStatus(Principal principal,
											@PathVariable Long storeId,
											@RequestBody @Valid StoreStatusChangeRequestDTO req) {

		Long storeOwnerId = ownerContext.requireStoreOwnerId(principal);

		storeService.changeStoreStatus(storeOwnerId, storeId, req);

		return ResponseEntity.ok(ApiResponse.ok(null, "store status updated"));
	}
}
