package com.deliveryapp.catchabite.controller;

import com.deliveryapp.catchabite.dto.StoreDTO;
import com.deliveryapp.catchabite.service.StoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/owner/stores")
public class OwnerStoreController {

	private final StoreService storeService;

	// 매장 정보 조회
	@GetMapping("/{storeId}")
	public ResponseEntity<?> getStoreInfo(@RequestHeader("storeOwnerId") Long storeOwnerId,
										  @PathVariable Long storeId) {

		StoreDTO store = storeService.getStoreInfo(storeOwnerId, storeId);
		return ResponseEntity.ok(store);
	}

	// 매장 등록
	@PostMapping
	public ResponseEntity<?> createStore(@RequestHeader("storeOwnerId") Long storeOwnerId,
										 @RequestBody StoreDTO dto) {

		StoreDTO result = storeService.createStore(storeOwnerId, dto);
		return ResponseEntity.ok(result);
	}

	// 매장 기본 정보 수정
	@PutMapping("/{storeId}")
	public ResponseEntity<?> updateStore(@RequestHeader("storeOwnerId") Long storeOwnerId,
										 @PathVariable Long storeId,
										 @RequestBody StoreDTO dto) {

		StoreDTO result = storeService.updateStoreBasicInfo(storeOwnerId, storeId, dto);
		return ResponseEntity.ok(result);
	}

	// 영업 상태 변경
	@PatchMapping("/{storeId}/status")
	public ResponseEntity<?> changeStoreStatus(@RequestHeader("storeOwnerId") Long storeOwnerId,
											   @PathVariable Long storeId,
											   @RequestBody StoreDTO dto) {

		storeService.changeStoreStatus(storeOwnerId, storeId, dto.getStoreOpenStatus());

		return ResponseEntity.ok(Map.of(
				"storeId", storeId,
				"storeOpenStatus", dto.getStoreOpenStatus()
		));
	}
}
