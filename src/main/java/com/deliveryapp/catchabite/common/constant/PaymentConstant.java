package com.deliveryapp.catchabite.common.constant;

/**
 * PaymentConstant: 결제 관련 상수 정의
 * 
 * Description: 결제 시스템에서 사용되는 모든 상수를 중앙 집중식으로 관리합니다.
 * PortOne API, 결제 상태, 에러 메시지 등을 정의합니다.
 * 
 * Required Variables/Parameters: (상수만 정의)
 * 
 * Output/Data Flow: 다른 결제 관련 클래스에서 참조됩니다.
 * 
 * Dependencies: 없음 (standalone constant class)
 */

public class PaymentConstant {
    
    // ========== PortOne API 관련 상수 ==========
    public static final String PORTONE_API_BASE_URL = "https://api.portone.io";
    public static final String PORTONE_PAYMENT_GET_ENDPOINT = "/payments/{imp_uid}";
    public static final String PORTONE_CANCEL_PAYMENT_ENDPOINT = "/payments/cancel";
    public static final String PORTONE_ACCESS_TOKEN_ENDPOINT = "/users/getToken";
    
    // ========== 결제 상태 (Payment Status) ==========
    public static final String PAYMENT_STATUS_PENDING = "PENDING";      // 결제 대기
    public static final String PAYMENT_STATUS_PAID = "PAID";            // 결제 완료
    public static final String PAYMENT_STATUS_FAILED = "FAILED";        // 결제 실패
    public static final String PAYMENT_STATUS_CANCELLED = "CANCELLED";  // 취소됨
    public static final String PAYMENT_STATUS_REFUNDED = "REFUNDED";    // 환불됨
    
    // ========== 주문 상태 (Order Status) ==========
    public static final String ORDER_STATUS_PENDING = "PENDING";        // 대기중
    public static final String ORDER_STATUS_CONFIRMED = "CONFIRMED";    // 확인됨
    public static final String ORDER_STATUS_IN_PROGRESS = "IN_PROGRESS"; // 진행중
    public static final String ORDER_STATUS_CANCELLED = "CANCELLED";    // 취소됨
    
    // ========== 결제 방법 ==========
    public static final String PAYMENT_METHOD_CARD = "card";
    public static final String PAYMENT_METHOD_TRANSFER = "transfer";
    public static final String PAYMENT_METHOD_VBANK = "vbank";
    
    // ========== 거래 타입 (Transaction Type) ==========
    public static final String TRANSACTION_TYPE_USER_PAYMENT = "USERPAYMENT";
    public static final String TRANSACTION_TYPE_STORE_PAYOUT = "STOREPAYOUT";
    public static final String TRANSACTION_TYPE_DELIVERY_PAYOUT = "DELIVERYPAYOUT";
    
    // ========== 거래 상태 (Transaction Status) ==========
    public static final String TRANSACTION_STATUS_PENDING = "PENDING";
    public static final String TRANSACTION_STATUS_COMPLETED = "COMPLETED";
    public static final String TRANSACTION_STATUS_FAILED = "FAILED";
    public static final String TRANSACTION_STATUS_REFUNDED = "REFUNDED";
    
    // ========== 에러 메시지 ==========
    public static final String ERROR_ORDER_NOT_FOUND = "주문을 찾을 수 없습니다.";
    public static final String ERROR_PAYMENT_NOT_FOUND = "결제 정보를 찾을 수 없습니다.";
    public static final String ERROR_AMOUNT_MISMATCH = "결제 금액이 주문 금액과 맞지 않습니다.";
    public static final String ERROR_INVALID_PAYMENT_STATUS = "유효하지 않은 결제 상태입니다.";
    public static final String ERROR_PORTONE_API_FAILED = "PortOne API 호출 실패.";
    public static final String ERROR_PAYMENT_ALREADY_COMPLETED = "이미 완료된 결제입니다.";
    public static final String ERROR_INVALID_MERCHANT_UID = "유효하지 않은 주문번호입니다.";
    
    // ========== HTTP 헤더 ==========
    public static final String HEADER_AUTHORIZATION = "Authorization";
    public static final String HEADER_CONTENT_TYPE = "Content-Type";
    public static final String CONTENT_TYPE_JSON = "application/json";
    
    // ========== 타임아웃 (밀리초) ==========
    public static final int PORTONE_API_TIMEOUT_MILLISECONDS = 5000;
    
    // ========== 환율 (예: 1 KRW = 1 최소 단위) ==========
    public static final int KRW_UNIT = 1;
}