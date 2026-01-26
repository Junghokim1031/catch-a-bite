package com.deliveryapp.catchabite.controller;

import com.deliveryapp.catchabite.common.response.ApiResponse;
import com.deliveryapp.catchabite.dto.MenuOptionDTO;
import com.deliveryapp.catchabite.dto.MenuOptionGroupDTO;
import com.deliveryapp.catchabite.security.OwnerContext;
import com.deliveryapp.catchabite.service.MenuOptionGroupService;
import com.deliveryapp.catchabite.service.MenuOptionService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/owner/menus/{menuId}/option-groups")
public class OwnerMenuOptionController {

	private final MenuOptionGroupService menuOptionGroupService;
	private final MenuOptionService menuOptionService;
	private final OwnerContext ownerContext;


	// 옵션 그룹 

	// 옵션 그룹 목록 조회
	@GetMapping
	public ResponseEntity<ApiResponse<List<MenuOptionGroupDTO>>> listOptionGroups(
			Principal principal,
			@PathVariable Long menuId
	) {
		Long storeOwnerId = ownerContext.requireStoreOwnerId(principal);
		List<MenuOptionGroupDTO> result = menuOptionGroupService.listOptionGroups(storeOwnerId, menuId);
		return ResponseEntity.ok(ApiResponse.ok(result));
	}


	// 옵션 그룹 등록
	@PostMapping
	public ResponseEntity<ApiResponse<Object>> createOptionGroup(
			Principal principal,
			@PathVariable Long menuId,
			@RequestBody MenuOptionGroupDTO dto
	) {
		Long storeOwnerId = ownerContext.requireStoreOwnerId(principal);
		menuOptionGroupService.createOptionGroup(storeOwnerId, menuId, dto);
		return ResponseEntity.ok(ApiResponse.ok(null, "option group created"));
	}

	// 옵션 그룹 수정
	@PutMapping("/{menuOptionGroupId}")
	public ResponseEntity<ApiResponse<Object>> updateOptionGroup(
			Principal principal,
			@PathVariable Long menuId,
			@PathVariable Long menuOptionGroupId,
			@RequestBody MenuOptionGroupDTO dto
	) {
		Long storeOwnerId = ownerContext.requireStoreOwnerId(principal);
		menuOptionGroupService.updateOptionGroup(storeOwnerId, menuId, menuOptionGroupId, dto);
		return ResponseEntity.ok(ApiResponse.ok(null, "option group updated"));
	}

	// 옵션 그룹 삭제
	@DeleteMapping("/{menuOptionGroupId}")
	public ResponseEntity<ApiResponse<Object>> deleteOptionGroup(
			Principal principal,
			@PathVariable Long menuId,
			@PathVariable Long menuOptionGroupId
	) {
		Long storeOwnerId = ownerContext.requireStoreOwnerId(principal);
		menuOptionGroupService.deleteOptionGroup(storeOwnerId, menuId, menuOptionGroupId);
		return ResponseEntity.ok(ApiResponse.ok(null, "option group deleted"));
	}


	// 옵션 "항목" 추가
	

	// 옵션 항목 등록
	@PostMapping("/{menuOptionGroupId}/options")
	public ResponseEntity<ApiResponse<Object>> createOption(
			Principal principal,
			@PathVariable Long menuId,
			@PathVariable Long menuOptionGroupId,
			@RequestBody MenuOptionDTO dto
	) {
		Long storeOwnerId = ownerContext.requireStoreOwnerId(principal);
		menuOptionService.createOption(storeOwnerId, menuId, menuOptionGroupId, dto);
		return ResponseEntity.ok(ApiResponse.ok(null, "option created"));
	}

	// 옵션 항목 목록 조회
	@GetMapping("/{menuOptionGroupId}/options")
	public ResponseEntity<ApiResponse<List<MenuOptionDTO>>> listOptions(
			Principal principal,
			@PathVariable Long menuId,
			@PathVariable Long menuOptionGroupId
	) {
		Long storeOwnerId = ownerContext.requireStoreOwnerId(principal);
		List<MenuOptionDTO> result = menuOptionService.listOptions(storeOwnerId, menuId, menuOptionGroupId);
		return ResponseEntity.ok(ApiResponse.ok(result));
	}

	// 옵션 항목 수정
	@PutMapping("/{menuOptionGroupId}/options/{menuOptionId}")
	public ResponseEntity<ApiResponse<Object>> updateOption(
			Principal principal,
			@PathVariable Long menuId,
			@PathVariable Long menuOptionGroupId,
			@PathVariable Long menuOptionId,
			@RequestBody MenuOptionDTO dto
	) {
		Long storeOwnerId = ownerContext.requireStoreOwnerId(principal);
		menuOptionService.updateOption(storeOwnerId, menuId, menuOptionGroupId, menuOptionId, dto);
		return ResponseEntity.ok(ApiResponse.ok(null, "option updated"));
	}

	// 옵션 항목 삭제
	@DeleteMapping("/{menuOptionGroupId}/options/{menuOptionId}")
	public ResponseEntity<ApiResponse<Object>> deleteOption(
			Principal principal,
			@PathVariable Long menuId,
			@PathVariable Long menuOptionGroupId,
			@PathVariable Long menuOptionId
	) {
		Long storeOwnerId = ownerContext.requireStoreOwnerId(principal);
		menuOptionService.deleteOption(storeOwnerId, menuId, menuOptionGroupId, menuOptionId);
		return ResponseEntity.ok(ApiResponse.ok(null, "option deleted"));
	}
}
