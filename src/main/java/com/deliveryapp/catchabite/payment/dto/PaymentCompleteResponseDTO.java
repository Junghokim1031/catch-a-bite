package com.deliveryapp.catchabite.payment.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import java.time.LocalDateTime;


/**
 * PaymentCompleteResponseDTO: 결제 완료 후 프론트엔드에 보내는 최종 응답 (PortOne V2)
 * 
 * Description: PortOne V2 결제 검증 완료 후 최종 결과를 프론트엔드에 반환합니다.
 * 결제 성공/실패 여부와 상세 정보를 포함합니다.
 * 
 * Required Variables/Parameters:
 * - success (boolean): 결제 성공 여부
 * - message (String): 결과 메시지
 * - paymentId (Long): PortOne V2 payment_id (numeric)
 * - orderId (Long): StoreOrder.orderId
 * 
 * Output/Data Flow:
 * - Sent to React Native from /api/payments/complete endpoint
 * - Used by frontend to show payment result UI
 * - Success case: includes payment details
 * - Failure case: includes error_code and error_message
 * 
 * Dependencies: Lombok, Jackson
 * 
 * Example Success Response (200 OK):
 * {
 *   "success": true,
 *   "message": "결제가 성공적으로 완료되었습니다.",
 *   "payment_id": 1234567890,
 *   "order_id": 2,
 *   "payment_status": "PAID",
 *   "payment_amount": 28000,
 *   "payment_method": "card",
 *   "payment_paid_at": "2024-01-14T12:30:00"
 * }
 * 
 * Example Failure Response (400 Bad Request):
 * {
 *   "success": false,
 *   "message": "오류가 발생했습니다.",
 *   "error_code": "AMOUNT_MISMATCH",
 *   "error_message": "금액이 일치하지 않습니다. PortOne: 28000, DB: 25000"
 * }
 */


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentCompleteResponseDTO {
    
    /**
     * 결제 성공 여부
     * true: 결제 성공 (PAID)
     * false: 결제 실패 또는 검증 실패
     */
    @JsonProperty("success")
    private boolean success;
    
    /**
     * 결과 메시지
     * 사용자 친화적 메시지
     * Success: "결제가 성공적으로 완료되었습니다."
     * Failure: "오류가 발생했습니다."
     */
    @JsonProperty("message")
    private String message;
    
    /**
     * Payment.paymentId (DB에서 저장된 결제 ID)
     * V2: PortOne V2 API에서 반환한 numeric payment_id
     * 예: 1234567890
     */
    @JsonProperty("payment_id")
    private Long paymentId;
    
    /**
     * StoreOrder.orderId (주문 ID)
     * 우리 서버 DB의 주문 ID
     */
    @JsonProperty("order_id")
    private Long orderId;
    
    /**
     * 결제 상태
     * PAID: 결제 완료
     * FAILED: 결제 실패
     * CANCELLED: 결제 취소
     * PENDING: 대기중 (일반적으로 응답에 포함되지 않음)
     */
    @JsonProperty("payment_status")
    private String paymentStatus;
    
    /**
     * 결제 금액 (단위: KRW)
     * 원(₩) 단위의 결제 금액
     * 예: 28000 (28,000원)
     */
    @JsonProperty("payment_amount")
    private Long paymentAmount;
    
    /**
     * 결제 방법
     * card: 신용카드
     * bank: 계좌이체
     * vbank: 가상계좌
     * payco: 페이코
     * 등등...
     */
    @JsonProperty("payment_method")
    private String paymentMethod;
    
    /**
     * 결제 시간 (Asia/Seoul 기준)
     * PortOne V2 paid_at (Unix timestamp)를 LocalDateTime으로 변환
     * 예: 2024-01-14T12:30:45.123456
     */
    @JsonProperty("payment_paid_at")
    private LocalDateTime paymentPaidAt;
    
    /**
     * 에러 코드 (실패 시에만 포함)
     * AMOUNT_MISMATCH: 금액 불일치
     * PAYMENT_NOT_FOUND: 결제 정보 없음
     * PAYMENT_ALREADY_PAID: 이미 완료된 결제
     * INVALID_MERCHANT_UID: 유효하지 않은 주문 번호
     * INTERNAL_ERROR: 서버 내부 오류
     * 등등...
     */
    @JsonProperty("error_code")
    private String errorCode;
    
    /**
     * 에러 메시지 (실패 시에만 포함)
     * 에러 상세 설명 (사용자 친화적)
     * 예: "금액이 일치하지 않습니다. PortOne: 28000, DB: 25000"
     */
    @JsonProperty("error_message")
    private String errorMessage;
}