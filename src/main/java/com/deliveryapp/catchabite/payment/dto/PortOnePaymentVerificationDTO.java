package com.deliveryapp.catchabite.payment.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

/**
 * PortOnePaymentVerificationDTO: PortOne API 결제 조회 응답 (V2)
 * * Updated for PortOne V2 API structure (Flat JSON).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true) // V2 응답의 불필요한 필드 무시
public class PortOnePaymentVerificationDTO {

    /**
     * PortOne 결제 ID (V2)
     * JSON field: "id"
     */
    @JsonProperty("id")
    private String paymentId;

    /**
     * 결제 상태
     * - PAID, CANCELLED, FAILED, etc.
     */
    @JsonProperty("status")
    private String status;

    /**
     * 결제 금액 정보 (Nested Object)
     */
    @JsonProperty("amount")
    private PaymentAmount amount;

    /**
     * 결제 수단 정보 (Nested Object)
     */
    @JsonProperty("method")
    private PaymentMethod method;

    /**
     * 결제 완료 시간 (ISO 8601 String)
     * 예: "2026-01-19T04:54:08.323Z"
     */
    @JsonProperty("paidAt")
    private String paidAt;

    /**
     * 실패 사유 (if any)
     * Note: V2 json might put this in a different place, but often used in errors
     */
    @JsonProperty("cancellation")
    private PaymentCancellation cancellation;

    // --- Nested Classes ---

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PaymentAmount {
        @JsonProperty("total")
        private Long total;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PaymentMethod {
        @JsonProperty("type")
        private String type;
        
        @JsonProperty("provider")
        private String provider;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PaymentCancellation {
        @JsonProperty("reason")
        private String reason;
    }
}