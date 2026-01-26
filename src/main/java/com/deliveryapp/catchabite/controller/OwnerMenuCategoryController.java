package com.deliveryapp.catchabite.controller;

import com.deliveryapp.catchabite.common.response.ApiResponse;
import com.deliveryapp.catchabite.dto.MenuCategoryDTO;
import com.deliveryapp.catchabite.security.OwnerContext;
import com.deliveryapp.catchabite.service.MenuCategoryService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/owner/stores/{storeId}/menu-categories")
public class OwnerMenuCategoryController {

	private final MenuCategoryService menuCategoryService;
	private final OwnerContext ownerContext;

	// 카테고리 목록
	@GetMapping
	public ResponseEntity<ApiResponse<List<MenuCategoryDTO>>> getMenuCategories(Principal principal,
									   @PathVariable Long storeId) {

		Long storeOwnerId = ownerContext.requireStoreOwnerId(principal);

		List<MenuCategoryDTO> categories =
				menuCategoryService.getMenuCategories(storeOwnerId, storeId);

		return ResponseEntity.ok(ApiResponse.ok(categories));
	}

	// 카테고리 등록
	@PostMapping
	public ResponseEntity<ApiResponse<MenuCategoryDTO>> createMenuCategory(Principal principal,
										@PathVariable Long storeId,
												@RequestBody MenuCategoryDTO dto) {

		Long storeOwnerId = ownerContext.requireStoreOwnerId(principal);

		MenuCategoryDTO result =
				menuCategoryService.createMenuCategory(storeOwnerId, storeId, dto);

		return ResponseEntity.ok(ApiResponse.ok(result));
	}

	// 카테고리 수정
	@PutMapping("/{menuCategoryId}")
	public ResponseEntity<ApiResponse<MenuCategoryDTO>> updateMenuCategory(Principal principal,
												@PathVariable Long storeId,
												@PathVariable Long menuCategoryId,
												@RequestBody MenuCategoryDTO dto) {

		Long storeOwnerId = ownerContext.requireStoreOwnerId(principal);

		MenuCategoryDTO result =
				menuCategoryService.updateMenuCategory(storeOwnerId, storeId, menuCategoryId, dto);

		return ResponseEntity.ok(ApiResponse.ok(result));
	}

	// 카테고리 삭제
	@DeleteMapping("/{menuCategoryId}")
	public ResponseEntity<ApiResponse<Object>> deleteMenuCategory(Principal principal,
												@PathVariable Long storeId,
												@PathVariable Long menuCategoryId) {

		Long storeOwnerId = ownerContext.requireStoreOwnerId(principal);

		menuCategoryService.deleteMenuCategory(storeOwnerId, storeId, menuCategoryId);

		return ResponseEntity.ok(ApiResponse.ok(null, "menu category deleted"));
	}
}
