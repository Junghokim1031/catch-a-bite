package com.deliveryapp.catchabite.controller;

import com.deliveryapp.catchabite.common.response.ApiResponse;
import com.deliveryapp.catchabite.dto.StoreImageDTO;
import com.deliveryapp.catchabite.security.OwnerContext;
import com.deliveryapp.catchabite.service.StoreImageService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/owner/stores/{storeId}/images")
public class OwnerStoreImageController {

	private final StoreImageService storeImageService;
	private final OwnerContext ownerContext;

	// 이미지 목록 조회
	@GetMapping
	public ResponseEntity<ApiResponse<List<StoreImageDTO>>> getStoreImages(Principal principal,
									@PathVariable Long storeId) {

		Long storeOwnerId = ownerContext.requireStoreOwnerId(principal);

		List<StoreImageDTO> images =
				storeImageService.getStoreImages(storeOwnerId, storeId);

		return ResponseEntity.ok(ApiResponse.ok(images));
	}

	// 이미지 등록
	@PostMapping
	public ResponseEntity<ApiResponse<StoreImageDTO>> createStoreImage(Principal principal,
											  @PathVariable Long storeId,
											  @RequestBody StoreImageDTO dto) {

		Long storeOwnerId = ownerContext.requireStoreOwnerId(principal);

		StoreImageDTO result =
				storeImageService.createStoreImage(storeOwnerId, storeId, dto);

		return ResponseEntity.ok(ApiResponse.ok(result));
	}

	// 이미지 삭제
	@DeleteMapping("/{storeImageId}")
	public ResponseEntity<ApiResponse<Object>> deleteStoreImage(Principal principal,
											  @PathVariable Long storeId,
											  @PathVariable Long storeImageId) {

		Long storeOwnerId = ownerContext.requireStoreOwnerId(principal);

		storeImageService.deleteStoreImage(storeOwnerId, storeId, storeImageId);

		return ResponseEntity.ok(ApiResponse.ok(null, "store image deleted"));
	}
}
