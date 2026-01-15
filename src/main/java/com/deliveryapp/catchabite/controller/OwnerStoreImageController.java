package com.deliveryapp.catchabite.controller;

import com.deliveryapp.catchabite.dto.StoreImageDTO;
import com.deliveryapp.catchabite.service.StoreImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/owner/stores/{storeId}/images")
public class OwnerStoreImageController {

	private final StoreImageService storeImageService;

	// 이미지 목록 조회
	@GetMapping
	public ResponseEntity<?> getStoreImages(@RequestHeader("storeOwnerId") Long storeOwnerId,
											@PathVariable Long storeId) {

		List<StoreImageDTO> images =
				storeImageService.getStoreImages(storeOwnerId, storeId);

		return ResponseEntity.ok(images);
	}

	// 이미지 등록
	@PostMapping
	public ResponseEntity<?> createStoreImage(@RequestHeader("storeOwnerId") Long storeOwnerId,
											  @PathVariable Long storeId,
											  @RequestBody StoreImageDTO dto) {

		StoreImageDTO result =
				storeImageService.createStoreImage(storeOwnerId, storeId, dto);

		return ResponseEntity.ok(result);
	}

	// 이미지 삭제
	@DeleteMapping("/{storeImageId}")
	public ResponseEntity<?> deleteStoreImage(@RequestHeader("storeOwnerId") Long storeOwnerId,
											  @PathVariable Long storeId,
											  @PathVariable Long storeImageId) {

		storeImageService.deleteStoreImage(storeOwnerId, storeId, storeImageId);

		return ResponseEntity.ok(Map.of(
				"deleted", true,
				"storeImageId", storeImageId
		));
	}
}
