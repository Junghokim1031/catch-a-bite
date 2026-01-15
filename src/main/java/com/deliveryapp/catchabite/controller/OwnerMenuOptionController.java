package com.deliveryapp.catchabite.controller;

import com.deliveryapp.catchabite.dto.MenuOptionDTO;
import com.deliveryapp.catchabite.dto.MenuOptionGroupDTO;
import com.deliveryapp.catchabite.service.MenuOptionGroupService;
import com.deliveryapp.catchabite.service.MenuOptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/owner/menus/{menuId}/option-groups")
public class OwnerMenuOptionController {

	private final MenuOptionGroupService menuOptionGroupService;
	private final MenuOptionService menuOptionService;


	// 옵션 그룹 


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


	// 옵션 "항목" 추가
	

	// 옵션 항목 등록
	@PostMapping("/{menuOptionGroupId}/options")
	public ResponseEntity<?> createOption(
			@RequestHeader("storeOwnerId") Long storeOwnerId,
			@PathVariable Long menuId,
			@PathVariable Long menuOptionGroupId,
			@RequestBody MenuOptionDTO dto
	) {
		menuOptionService.createOption(storeOwnerId, menuId, menuOptionGroupId, dto);
		return ResponseEntity.ok().build();
	}

	// 옵션 항목 목록 조회
	@GetMapping("/{menuOptionGroupId}/options")
	public ResponseEntity<List<MenuOptionDTO>> listOptions(
			@RequestHeader("storeOwnerId") Long storeOwnerId,
			@PathVariable Long menuId,
			@PathVariable Long menuOptionGroupId
	) {
		List<MenuOptionDTO> result = menuOptionService.listOptions(storeOwnerId, menuId, menuOptionGroupId);
		return ResponseEntity.ok(result);
	}

	// 옵션 항목 수정
	@PutMapping("/{menuOptionGroupId}/options/{menuOptionId}")
	public ResponseEntity<?> updateOption(
			@RequestHeader("storeOwnerId") Long storeOwnerId,
			@PathVariable Long menuId,
			@PathVariable Long menuOptionGroupId,
			@PathVariable Long menuOptionId,
			@RequestBody MenuOptionDTO dto
	) {
		menuOptionService.updateOption(storeOwnerId, menuId, menuOptionGroupId, menuOptionId, dto);
		return ResponseEntity.ok().build();
	}

	// 옵션 항목 삭제
	@DeleteMapping("/{menuOptionGroupId}/options/{menuOptionId}")
	public ResponseEntity<?> deleteOption(
			@RequestHeader("storeOwnerId") Long storeOwnerId,
			@PathVariable Long menuId,
			@PathVariable Long menuOptionGroupId,
			@PathVariable Long menuOptionId
	) {
		menuOptionService.deleteOption(storeOwnerId, menuId, menuOptionGroupId, menuOptionId);
		return ResponseEntity.ok().build();
	}
}
