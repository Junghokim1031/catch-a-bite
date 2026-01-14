package com.deliveryapp.catchabite.controller;

import com.deliveryapp.catchabite.dto.MenuCategoryDTO;
import com.deliveryapp.catchabite.service.MenuCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/owner/stores/{storeId}/menu-categories")
public class OwnerMenuCategoryController {

	private final MenuCategoryService menuCategoryService;

	// 카테고리 목록
	@GetMapping
	public ResponseEntity<?> getMenuCategories(@RequestHeader("storeOwnerId") Long storeOwnerId,
											   @PathVariable Long storeId) {

		List<MenuCategoryDTO> categories =
				menuCategoryService.getMenuCategories(storeOwnerId, storeId);

		return ResponseEntity.ok(categories);
	}

	// 카테고리 등록
	@PostMapping
	public ResponseEntity<?> createMenuCategory(@RequestHeader("storeOwnerId") Long storeOwnerId,
												@PathVariable Long storeId,
												@RequestBody MenuCategoryDTO dto) {

		MenuCategoryDTO result =
				menuCategoryService.createMenuCategory(storeOwnerId, storeId, dto);

		return ResponseEntity.ok(result);
	}

	// 카테고리 수정
	@PutMapping("/{menuCategoryId}")
	public ResponseEntity<?> updateMenuCategory(@RequestHeader("storeOwnerId") Long storeOwnerId,
												@PathVariable Long storeId,
												@PathVariable Long menuCategoryId,
												@RequestBody MenuCategoryDTO dto) {

		MenuCategoryDTO result =
				menuCategoryService.updateMenuCategory(storeOwnerId, storeId, menuCategoryId, dto);

		return ResponseEntity.ok(result);
	}

	// 카테고리 삭제
	@DeleteMapping("/{menuCategoryId}")
	public ResponseEntity<?> deleteMenuCategory(@RequestHeader("storeOwnerId") Long storeOwnerId,
												@PathVariable Long storeId,
												@PathVariable Long menuCategoryId) {

		menuCategoryService.deleteMenuCategory(storeOwnerId, storeId, menuCategoryId);

		return ResponseEntity.ok(Map.of(
				"deleted", true,
				"menuCategoryId", menuCategoryId
		));
	}
}
