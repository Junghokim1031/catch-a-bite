package com.deliveryapp.catchabite.controller;

import com.deliveryapp.catchabite.common.response.ApiResponse;
import com.deliveryapp.catchabite.dto.MenuDTO;
import com.deliveryapp.catchabite.security.OwnerContext;
import com.deliveryapp.catchabite.service.MenuService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/owner/stores/{storeId}/menus")
public class OwnerMenuController {

	private final MenuService menuService;
	private final OwnerContext ownerContext;

	// 메뉴 목록 조회
	@GetMapping
	public ResponseEntity<ApiResponse<List<MenuDTO>>> getMenus(Principal principal,
									  @PathVariable Long storeId,
									  @RequestParam(required = false) Long menuCategoryId) {

		Long storeOwnerId = ownerContext.requireStoreOwnerId(principal);

		List<MenuDTO> menus = menuService.getMenus(storeOwnerId, storeId, menuCategoryId);
		return ResponseEntity.ok(ApiResponse.ok(menus));
	}

	// 메뉴 등록
	@PostMapping
	public ResponseEntity<ApiResponse<MenuDTO>> createMenu(Principal principal,
										@PathVariable Long storeId,
										@RequestBody MenuDTO dto) {

		Long storeOwnerId = ownerContext.requireStoreOwnerId(principal);

		MenuDTO result = menuService.createMenu(storeOwnerId, storeId, dto);
		return ResponseEntity.ok(ApiResponse.ok(result));
	}

	// 메뉴 수정
	@PutMapping("/{menuId}")
	public ResponseEntity<ApiResponse<MenuDTO>> updateMenu(Principal principal,
										@PathVariable Long storeId,
										@PathVariable Long menuId,
										@RequestBody MenuDTO dto) {

		Long storeOwnerId = ownerContext.requireStoreOwnerId(principal);

		MenuDTO result = menuService.updateMenu(storeOwnerId, storeId, menuId, dto);
		return ResponseEntity.ok(ApiResponse.ok(result));
	}

	// 메뉴 판매 상태 변경
	@PatchMapping("/{menuId}/availability")
	public ResponseEntity<ApiResponse<Object>> changeAvailability(Principal principal,
								   @PathVariable Long storeId,
								   @PathVariable Long menuId,
								   @RequestBody java.util.Map<String, Boolean> body) {

		Long storeOwnerId = ownerContext.requireStoreOwnerId(principal);

		Boolean menuIsAvailable = body.get("menuIsAvailable");
		menuService.changeMenuAvailability(storeOwnerId, storeId, menuId, menuIsAvailable);
		return ResponseEntity.ok(ApiResponse.ok(null, "menu availability updated"));
	}

	// 메뉴 삭제
	@DeleteMapping("/{menuId}")
	public ResponseEntity<ApiResponse<Object>> deleteMenu(Principal principal,
										@PathVariable Long storeId,
										@PathVariable Long menuId) {

		Long storeOwnerId = ownerContext.requireStoreOwnerId(principal);

		menuService.deleteMenu(storeOwnerId, storeId, menuId);
		return ResponseEntity.ok(ApiResponse.ok(null, "menu deleted"));
	}
}
