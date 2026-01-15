package com.deliveryapp.catchabite.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OwnerOrderDTO {

	// --- 공통(목록/상세) ---
	private Long orderId;
	private Long storeId;

	private String orderStatus;          // pending/cooking/cooked/delivered/rejected (enum 문자열)
	private LocalDateTime orderCreatedAt;

	private Long orderTotalPrice;     // 총 결제금액(또는 총 주문금액)
	private Long deliveryFee;         // 배달비(있다면)
	private Long discountPrice;       // 할인(있다면)

	// --- 주문자/연락처/요청사항 ---
	private String orderCustomerName;    // 주문자명(스냅샷)
	private String orderCustomerPhone;   // 연락처(스냅샷)
	private String orderRequestMessage;  // 요청사항(있다면)

	// --- 주소/배달 정보(상세에서 주로 사용) ---
	private String orderAddress;         // 주소(스냅샷)
	private String orderAddressDetail;   // 상세주소
	private String orderDeliveryMemo;    // 배달메모

	// --- 거절 사유(거절 상태일 때) ---
	private String rejectReason;

	// --- 상세 전용(목록에서는 null/empty) ---
	private List<ItemDTO> items;

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class ItemDTO {
		private Long orderItemId;

		private Long menuId;
		private String menuName;          // 스냅샷
		private Long menuPrice;        // 스냅샷
		private Long quantity;

		private List<OptionDTO> options;
	}

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class OptionDTO {
		private Long orderOptionId;

		private Long menuOptionGroupId;
		private String menuOptionGroupName; // 스냅샷(없으면 빼도 됨)

		private Long menuOptionId;
		private String menuOptionName;      // 스냅샷
		private Long menuOptionPrice;    // 스냅샷
	}
}
