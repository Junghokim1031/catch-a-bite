package com.deliveryapp.catchabite.payment.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

/**
 * PortOnePaymentVerificationDTO: PortOne API 결제 조회 응답 (V2)
 *
 * Description: PortOne 결제 조회 API 응답을 매핑합니다.
 * V2에서는 결제 식별자로 imp_uid가 아니라 payment_id를 사용합니다.
 *
 * Required Variables/Parameters:
 * - code (int): PortOne API 응답 코드 (0 = 성공)
 * - message (String): PortOne API 응답 메시지
 * - response (PaymentData): 실제 결제 데이터
 *
 * Output/Data Flow:
 * - Receives from PortOne payment 조회 API
 * - Processed by PaymentVerificationService
 *
 * Dependencies: Lombok, Jackson
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PortOnePaymentVerificationDTO {

    /**
     * PortOne API 응답 코드 (0 = 성공)
     */
    @JsonProperty("code")
    private int code;

    /**
     * PortOne API 응답 메시지
     */
    @JsonProperty("message")
    private String message;

    /**
     * PortOne 결제 데이터
     */
    @JsonProperty("response")
    private PaymentData response;

    /**
     * PortOne의 실제 결제 정보
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PaymentData {

        /**
         * PortOne 결제 ID (V2)
         * - V1의 imp_uid를 대체
         */
        @JsonProperty("payment_id")
        private Long paymentId;

        /**
         * 상점 주문 번호 (merchant_uid)
         */
        @JsonProperty("merchant_uid")
        private String merchantUid;

        /**
         * 결제액 (단위: KRW)
         */
        @JsonProperty("amount")
        private Long amount;

        /**
         * 결제 상태
         * - paid: 결제완료
         * - cancelled: 취소됨
         * - failed: 실패
         */
        @JsonProperty("status")
        private String status;

        /**
         * 결제 방법
         */
        @JsonProperty("pay_method")
        private String payMethod;

        /**
         * 카드 번호 (마스킹된 형태)
         */
        @JsonProperty("card_number")
        private String cardNumber;

        /**
         * 구매자 이름
         */
        @JsonProperty("buyer_name")
        private String buyerName;

        /**
         * 구매자 이메일
         */
        @JsonProperty("buyer_email")
        private String buyerEmail;

        /**
         * 구매자 전화번호
         */
        @JsonProperty("buyer_tel")
        private String buyerTel;

        /**
         * 결제 완료 시간 (Unix timestamp)
         */
        @JsonProperty("paid_at")
        private Long paidAt;

        /**
         * 실패 사유 (실패 시에만)
         */
        @JsonProperty("fail_reason")
        private String failReason;

        /**
         * 실패 코드 (실패 시에만)
         */
        @JsonProperty("fail_code")
        private String failCode;
    }
}
