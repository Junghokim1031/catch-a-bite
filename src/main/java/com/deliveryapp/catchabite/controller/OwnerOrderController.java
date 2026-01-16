package com.deliveryapp.catchabite.controller;

import com.deliveryapp.catchabite.dto.OwnerOrderDTO;
import com.deliveryapp.catchabite.dto.OwnerOrderRejectDTO;
import com.deliveryapp.catchabite.security.OwnerContext;
import com.deliveryapp.catchabite.service.OwnerOrderService;
import com.deliveryapp.catchabite.util.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/owner/stores/{storeId}/orders")
public class OwnerOrderController {

	private final OwnerOrderService ownerOrderService;
	private final OwnerContext ownerContext;

	// 9-1) 주문 목록 조회
	@GetMapping
	public ResponseEntity<ApiResponse<List<OwnerOrderDTO>>> list(
			Principal principal,
			@PathVariable Long storeId,
			@RequestParam(required = false) String status
	) {
		Long storeOwnerId = ownerContext.requireStoreOwnerId(principal);
		return ResponseEntity.ok(ApiResponse.ok(ownerOrderService.listOrders(storeOwnerId, storeId, status)));
	}

	// 9-2) 주문 상세 조회
	@GetMapping("/{orderId}")
	public ResponseEntity<ApiResponse<OwnerOrderDTO>> detail(
			Principal principal,
			@PathVariable Long storeId,
			@PathVariable Long orderId
	) {
		Long storeOwnerId = ownerContext.requireStoreOwnerId(principal);
		return ResponseEntity.ok(ApiResponse.ok(ownerOrderService.getOrderDetail(storeOwnerId, storeId, orderId)));
	}

	// 9-3) 주문 승인(조리 시작)
	@PatchMapping("/{orderId}/accept")
	public ResponseEntity<ApiResponse<Object>> accept(
			Principal principal,
			@PathVariable Long storeId,
			@PathVariable Long orderId
	) {
		Long storeOwnerId = ownerContext.requireStoreOwnerId(principal);
		ownerOrderService.acceptOrder(storeOwnerId, storeId, orderId);
		return ResponseEntity.ok(ApiResponse.ok(null, "order accepted"));
	}

	// 9-4) 주문 거절(사유 전달)
	@PatchMapping("/{orderId}/reject")
	public ResponseEntity<ApiResponse<Object>> reject(
			Principal principal,
			@PathVariable Long storeId,
			@PathVariable Long orderId,
			@RequestBody OwnerOrderRejectDTO dto
	) {
		Long storeOwnerId = ownerContext.requireStoreOwnerId(principal);
		ownerOrderService.rejectOrder(storeOwnerId, storeId, orderId, dto.getReason());
		return ResponseEntity.ok(ApiResponse.ok(null, "order rejected"));
	}

	// 9-5) 조리 완료
	@PatchMapping("/{orderId}/cooked")
	public ResponseEntity<ApiResponse<Object>> cooked(
			Principal principal,
			@PathVariable Long storeId,
			@PathVariable Long orderId
	) {
		Long storeOwnerId = ownerContext.requireStoreOwnerId(principal);
		ownerOrderService.markCooked(storeOwnerId, storeId, orderId);
		return ResponseEntity.ok(ApiResponse.ok(null, "order cooked"));
	}

	// 9-6) 배달 완료
	@PatchMapping("/{orderId}/delivered")
	public ResponseEntity<ApiResponse<Object>> delivered(
			Principal principal,
			@PathVariable Long storeId,
			@PathVariable Long orderId
	) {
		Long storeOwnerId = ownerContext.requireStoreOwnerId(principal);
		ownerOrderService.markDelivered(storeOwnerId, storeId, orderId);
		return ResponseEntity.ok(ApiResponse.ok(null, "order delivered"));
	}
}
