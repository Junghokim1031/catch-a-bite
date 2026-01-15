package com.deliveryapp.catchabite.controller;

import com.deliveryapp.catchabite.dto.OwnerOrderDTO;
import com.deliveryapp.catchabite.dto.OwnerOrderRejectDTO;
import com.deliveryapp.catchabite.service.OwnerOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/owner/stores/{storeId}/orders")
public class OwnerOrderController {

	private final OwnerOrderService ownerOrderService;

	// 9-1) 주문 목록 조회
	@GetMapping
	public ResponseEntity<List<OwnerOrderDTO>> list(
			@RequestHeader("storeOwnerId") Long storeOwnerId,
			@PathVariable Long storeId,
			@RequestParam(required = false) String status
	) {
		return ResponseEntity.ok(ownerOrderService.listOrders(storeOwnerId, storeId, status));
	}

	// 9-2) 주문 상세 조회
	@GetMapping("/{orderId}")
	public ResponseEntity<OwnerOrderDTO> detail(
			@RequestHeader("storeOwnerId") Long storeOwnerId,
			@PathVariable Long storeId,
			@PathVariable Long orderId
	) {
		return ResponseEntity.ok(ownerOrderService.getOrderDetail(storeOwnerId, storeId, orderId));
	}

	// 9-3) 주문 승인(조리 시작)
	@PatchMapping("/{orderId}/accept")
	public ResponseEntity<?> accept(
			@RequestHeader("storeOwnerId") Long storeOwnerId,
			@PathVariable Long storeId,
			@PathVariable Long orderId
	) {
		ownerOrderService.acceptOrder(storeOwnerId, storeId, orderId);
		return ResponseEntity.ok().build();
	}

	// 9-4) 주문 거절(사유 전달)
	@PatchMapping("/{orderId}/reject")
	public ResponseEntity<?> reject(
			@RequestHeader("storeOwnerId") Long storeOwnerId,
			@PathVariable Long storeId,
			@PathVariable Long orderId,
			@RequestBody OwnerOrderRejectDTO dto
	) {
		ownerOrderService.rejectOrder(storeOwnerId, storeId, orderId, dto.getReason());
		return ResponseEntity.ok().build();
	}

	// 9-5) 조리 완료
	@PatchMapping("/{orderId}/cooked")
	public ResponseEntity<?> cooked(
			@RequestHeader("storeOwnerId") Long storeOwnerId,
			@PathVariable Long storeId,
			@PathVariable Long orderId
	) {
		ownerOrderService.markCooked(storeOwnerId, storeId, orderId);
		return ResponseEntity.ok().build();
	}

	// 9-6) 배달 완료
	@PatchMapping("/{orderId}/delivered")
	public ResponseEntity<?> delivered(
			@RequestHeader("storeOwnerId") Long storeOwnerId,
			@PathVariable Long storeId,
			@PathVariable Long orderId
	) {
		ownerOrderService.markDelivered(storeOwnerId, storeId, orderId);
		return ResponseEntity.ok().build();
	}
}
