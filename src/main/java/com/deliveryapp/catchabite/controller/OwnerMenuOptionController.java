package com.deliveryapp.catchabite.controller;

import com.deliveryapp.catchabite.dto.MenuOptionGroupDTO;
import com.deliveryapp.catchabite.service.MenuOptionGroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/owner/menus/{menuId}/option-groups")
public class OwnerMenuOptionController {

	private final MenuOptionGroupService menuOptionGroupService;

	// 옵션 그룹 등록
	@PostMapping
	public ResponseEntity<?> createOptionGroup(
			@RequestHeader("storeOwnerId") Long storeOwnerId,
			@PathVariable Long menuId,
			@RequestBody MenuOptionGroupDTO dto
	) {
		menuOptionGroupService.createOptionGroup(storeOwnerId, menuId, dto);
		return ResponseEntity.ok().build();
	}

	// 옵션 그룹 수정
	@PutMapping("/{menuOptionGroupId}")
	public ResponseEntity<?> updateOptionGroup(
			@RequestHeader("storeOwnerId") Long storeOwnerId,
			@PathVariable Long menuId,
			@PathVariable Long menuOptionGroupId,
			@RequestBody MenuOptionGroupDTO dto
	) {
		menuOptionGroupService.updateOptionGroup(storeOwnerId, menuId, menuOptionGroupId, dto);
		return ResponseEntity.ok().build();
	}

	// 옵션 그룹 삭제
	@DeleteMapping("/{menuOptionGroupId}")
	public ResponseEntity<?> deleteOptionGroup(
			@RequestHeader("storeOwnerId") Long storeOwnerId,
			@PathVariable Long menuId,
			@PathVariable Long menuOptionGroupId
	) {
		menuOptionGroupService.deleteOptionGroup(storeOwnerId, menuId, menuOptionGroupId);
		return ResponseEntity.ok().build();
	}
}
