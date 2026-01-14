package com.deliveryapp.catchabite.controller;

import com.deliveryapp.catchabite.dto.MenuDTO;
import com.deliveryapp.catchabite.service.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/owner/stores/{storeId}/menus")
public class OwnerMenuController {

	private final MenuService menuService;

	// 메뉴 목록 조회
	@GetMapping
	public ResponseEntity<?> getMenus(@RequestHeader("storeOwnerId") Long storeOwnerId,
									  @PathVariable Long storeId,
									  @RequestParam(required = false) Long menuCategoryId) {

		List<MenuDTO> menus = menuService.getMenus(storeOwnerId, storeId, menuCategoryId);
		return ResponseEntity.ok(menus);
	}

	// 메뉴 등록
	@PostMapping
	public ResponseEntity<?> createMenu(@RequestHeader("storeOwnerId") Long storeOwnerId,
										@PathVariable Long storeId,
										@RequestBody MenuDTO dto) {

		MenuDTO result = menuService.createMenu(storeOwnerId, storeId, dto);
		return ResponseEntity.ok(result);
	}

	// 메뉴 수정
	@PutMapping("/{menuId}")
	public ResponseEntity<?> updateMenu(@RequestHeader("storeOwnerId") Long storeOwnerId,
										@PathVariable Long storeId,
										@PathVariable Long menuId,
										@RequestBody MenuDTO dto) {

		MenuDTO result = menuService.updateMenu(storeOwnerId, storeId, menuId, dto);
		return ResponseEntity.ok(result);
	}

	// 메뉴 판매 상태 변경
	@PatchMapping("/{menuId}/availability")
	public ResponseEntity<?> changeAvailability(@RequestHeader("storeOwnerId") Long storeOwnerId,
											   @PathVariable Long storeId,
											   @PathVariable Long menuId,
											   @RequestBody MenuDTO dto) {

		menuService.changeMenuAvailability(storeOwnerId, storeId, menuId, dto.getMenuIsAvailable());
		return ResponseEntity.ok(Map.of("menuId", menuId, "menuIsAvailable", dto.getMenuIsAvailable()));
	}

	// 메뉴 삭제
	@DeleteMapping("/{menuId}")
	public ResponseEntity<?> deleteMenu(@RequestHeader("storeOwnerId") Long storeOwnerId,
										@PathVariable Long storeId,
										@PathVariable Long menuId) {

		menuService.deleteMenu(storeOwnerId, storeId, menuId);
		return ResponseEntity.ok(Map.of("deleted", true, "menuId", menuId));
	}
}
