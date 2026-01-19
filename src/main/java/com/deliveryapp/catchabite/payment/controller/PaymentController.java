package com.deliveryapp.catchabite.payment.controller;


import com.deliveryapp.catchabite.common.exception.PaymentException;
import com.deliveryapp.catchabite.entity.Payment;
import com.deliveryapp.catchabite.payment.dto.PortOnePaymentRequestDTO;
import com.deliveryapp.catchabite.payment.dto.PortOnePaymentResponseDTO;
import com.deliveryapp.catchabite.payment.service.PaymentService;
import com.deliveryapp.catchabite.payment.service.PaymentVerificationService;
import com.deliveryapp.catchabite.payment.dto.PaymentCompleteResponseDTO;


import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


/**
 * PaymentController: 결제 관련 HTTP 엔드포인트 관리
 * 
 * Description: 프론트엔드(payment_test.html)의 결제 요청을 처리하고 응답합니다.
 * PortOne V2 SDK와 연동하여 결제 준비, 완료 검증, 결과 조회 등의 엔드포인트를 제공합니다.
 * 
 * 주요 기능:
 * 1. 결제 준비 (preparePayment) - Payment 엔티티 생성, merchant_uid 발급
 * 2. 결제 검증 (completePayment) - PortOne API 검증, 금액 확인, 주문 상태 업데이트
 * 3. 결제 조회 (getPaymentDetails) - 특정 결제 정보 조회
 * 
 * Required Variables/Parameters:
 * - paymentService (PaymentService): 결제 준비 로직 담당
 * - paymentVerificationService (PaymentVerificationService): 결제 검증 및 PortOne API 통신
 * 
 * Output/Data Flow:
 * - Receives PortOnePaymentRequestDTO from React (payment_test.html)
 * - Calls PaymentService.preparePayment() → Creates Payment entity
 * - Returns PortOnePaymentResponseDTO with merchant_uid to frontend
 * - Receives payment completion request with imp_uid from PortOne
 * - Calls PaymentVerificationService.verifyAndCompletePayment()
 * - Returns PaymentCompleteResponseDTO (success/failure status)
 * - Updates StoreOrder status based on payment result
 * 
 * Dependencies: PaymentService, PaymentVerificationService, Log4j2, Spring Web
 * 
 * Flow Diagram:
 * Frontend (payment_test.html)
 *    ↓
 * POST /api/payments/prepare
 *    ↓
 * PaymentService.preparePayment()
 *    ↓
 * Create Payment(PENDING), return merchant_uid
 *    ↓
 * Frontend opens PortOne payment window
 *    ↓
 * User completes payment with credit card (KG Inicis)
 *    ↓
 * POST /api/payments/complete with imp_uid
 *    ↓
 * PaymentVerificationService.verifyAndCompletePayment()
 *    ↓
 * Call PortOne API, validate amount, update Payment(PAID)
 *    ↓
 * Return success/failure response
 */


@Log4j2
@RestController
@RequestMapping("/api/payments")
public class PaymentController {
    
    private final PaymentService paymentService;
    private final PaymentVerificationService paymentVerificationService;
    
    /**
     * 생성자 - Dependency Injection
     * Spring이 자동으로 PaymentService와 PaymentVerificationService를 주입합니다.
     * 
     * @param paymentService 결제 준비 서비스 (Payment 엔티티 생성)
     * @param paymentVerificationService 결제 검증 서비스 (PortOne API 호출, 검증)
     */
    public PaymentController(PaymentService paymentService,
                            PaymentVerificationService paymentVerificationService) {
        this.paymentService = paymentService;
        this.paymentVerificationService = paymentVerificationService;
    }
    
    /**
     * 1️⃣ 결제 준비 엔드포인트
     * POST /api/payments/prepare
     * 
     * 프론트엔드가 결제 창(PortOne)을 띄우기 전에 호출합니다.
     * 이 메서드는:
     * 1. 주문 정보 검증 (order_id, amount)
     * 2. Payment 엔티티 생성 (상태: PENDING)
     * 3. merchant_uid 생성 (결제 고유 번호)
     * 4. PortOne 결제 창에 필요한 정보 반환
     * 
     * ⚠️ 중요: 이 단계에서는 아직 실제 결제가 일어나지 않습니다.
     * 결제 상태는 /complete 엔드포인트에서만 PAID로 업데이트됩니다.
     * 
     * Request Example (from payment_test.html):
     * {
     *   "order_id": 2,
     *   "payment_amount": 28000,
     *   "payment_method": "card",
     *   "buyer_name": "테스트 사용자",
     *   "buyer_email": "test@example.com",
     *   "buyer_tel": "01012345678",
     *   "buyer_addr": "서울시 강남구",
     *   "name": "CatchABite Order #2"
     * }
     * 
     * Response Example (200 OK):
     * {
     *   "merchant_uid": "ORDER_2_1705341600000",
     *   "imp_key": "yostore-cce49c90-090c-4f5d-9dd9-8a576c6c6a08",
     *   "payment_amount": 28000,
     *   "buyer_name": "테스트 사용자",
     *   "buyer_email": "test@example.com",
     *   "buyer_tel": "01012345678",
     *   "api_endpoint": "https://api.iamport.kr",
     *   "prepared_at": 1705341600000
     * }
     * 
     * @param request 프론트엔드에서 보낸 결제 요청 정보 (PortOnePaymentRequestDTO)
     * @return PortOne 결제 창에 필요한 정보와 merchant_uid를 포함한 응답
     */
    @PostMapping("/prepare")
    public ResponseEntity<?> preparePayment(@RequestBody PortOnePaymentRequestDTO request) {
        try {
            log.info("========================= PaymentController.preparePayment() START =========================");
            log.info("결제 준비 요청 수신 - order_id: {}, amount: {}, method: {}", 
                    request.getOrderId(), request.getPaymentAmount(), request.getPaymentMethod());
            log.info("요청 상세 정보 - buyer_name: {}, buyer_email: {}, buyer_tel: {}", 
                    request.getBuyerName(), request.getBuyerEmail(), request.getBuyerTel());
            
            /**
             * 입력 검증 Step 1: Order ID 검증
             * - null 확인
             * - 양수 여부 확인
             * 부정확한 order_id는 주문 조회 실패로 이어지므로 필수 검증
             */
            if (request.getOrderId() == null || request.getOrderId() <= 0) {
                log.error("========================= PaymentController.preparePayment() ERROR =========================");
                log.error("[INVALID_ORDER_ID] order_id가 유효하지 않습니다. 받은 값: {}", request.getOrderId());
                log.error("요청된 order_id는 1 이상의 정수여야 합니다.");
                log.error("=========================================================================================");
                
                return ResponseEntity.badRequest()
                        .body(createErrorResponse("INVALID_ORDER_ID", 
                                "Order ID must be a positive integer (받은 값: " + request.getOrderId() + ")"));
            }
            
            /**
             * 입력 검증 Step 2: Payment Amount 검증
             * - null 확인
             * - 양수 여부 확인
             * - 최소 결제 금액 확인 (선택)
             * 부정확한 금액은 결제 실패 또는 사기로 이어질 수 있으므로 필수 검증
             */
            if (request.getPaymentAmount() == null || request.getPaymentAmount() <= 0) {
                log.error("========================= PaymentController.preparePayment() ERROR =========================");
                log.error("[INVALID_AMOUNT] 결제 금액이 유효하지 않습니다. 받은 값: {}", request.getPaymentAmount());
                log.error("결제 금액은 1 이상의 정수여야 합니다.");
                log.error("=========================================================================================");
                
                return ResponseEntity.badRequest()
                        .body(createErrorResponse("INVALID_AMOUNT", 
                                "Payment amount must be positive (받은 값: " + request.getPaymentAmount() + ")"));
            }
            
            // 추가 검증: 최소 결제 금액 (한국 최소 결제 금액은 보통 100원)
            if (request.getPaymentAmount() < 100) {
                log.warn("========================= PaymentController.preparePayment() WARNING =========================");
                log.warn("[MINIMUM_AMOUNT_EXCEEDED] 최소 결제 금액 미만: {}", request.getPaymentAmount());
                log.warn("=========================================================================================");
                
                return ResponseEntity.badRequest()
                        .body(createErrorResponse("MINIMUM_AMOUNT_EXCEEDED", 
                                "Minimum payment amount is 100 KRW (받은 값: " + request.getPaymentAmount() + ")"));
            }
            
            log.info("입력 검증 완료 - order_id와 amount가 유효합니다.");
            log.info("PaymentService.preparePayment() 호출 중...");
            
            /**
             * Step 3: PaymentService를 통한 결제 준비
             * - Payment 엔티티 생성
             * - merchant_uid 생성
             * - 결제 상태를 PENDING으로 설정
             * - DB에 저장
             */
            PortOnePaymentResponseDTO response = paymentService.preparePayment(request);
            
            log.info("결제 준비 성공! merchant_uid: {}", response.getMerchantUid());
            log.info("생성된 Payment 엔티티 상태: PENDING");
            log.info("클라이언트에 다음 정보 반환:");
            log.info("  - merchant_uid: {}", response.getMerchantUid());
            log.info("  - payment_amount: {}", response.getPaymentAmount());
            log.info("  - buyer_name: {}", response.getBuyerName());
            log.info("========================= PaymentController.preparePayment() SUCCESS =========================");
            
            return ResponseEntity.ok(response);
            
        } catch (PaymentException pe) {
            /**
             * PaymentException 처리
             * 비즈니스 로직 에러 (예: 중복 결제, 주문 없음, 금액 불일치)
             * 클라이언트에 명확한 에러 메시지 반환
             */
            log.error("========================= PaymentController.preparePayment() ERROR =========================");
            log.error("[PaymentException] 결제 준비 실패");
            log.error("Error Code: {}", pe.getErrorCode());
            log.error("Error Message: {}", pe.getErrorMessage());
            log.error("발생 위치: PaymentService.preparePayment()");
            log.error("=========================================================================================");
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(createErrorResponse(pe.getErrorCode(), pe.getErrorMessage()));
        } catch (Exception e) {
            /**
             * 예상치 못한 에러 처리
             * 서버 내부 에러, DB 연결 실패 등
             * 클라이언트에는 일반적인 메시지만 반환 (보안)
             * 로그에는 상세한 스택트레이스 기록
             */
            log.error("========================= PaymentController.preparePayment() ERROR =========================");
            log.error("[INTERNAL_ERROR] 예상치 못한 오류 발생");
            log.error("Exception Type: {}", e.getClass().getName());
            log.error("Error Message: {}", e.getMessage());
            log.error("발생 위치: PaymentController.preparePayment() - 외부 라이브러리 호출 또는 DB 접근");
            log.error("=========================================================================================");
            log.error("Stack Trace: ", e);
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("INTERNAL_ERROR", 
                            "An unexpected error occurred. Please contact support."));
        }
    }
    
    
    @PostMapping("/complete")
    public ResponseEntity<?> completePayment(
            @RequestParam String paymentId,
            @RequestParam String merchantUid) {
        
        try {
            log.info("========================= PaymentController.completePayment() START =========================");
            log.info("결제 완료 요청 수신");
            log.info("  - paymentId: {} (PortOne V2 결제 ID)", paymentId);
            log.info("  - merchant_uid: {} (우리 서버 주문 ID)", merchantUid);
            
            /**
             * 입력 검증: paymentId 확인
             * - null 체크
             * - 빈 문자열 체크
             * paymentId가 없으면 PortOne V2 API 조회 불가능
             */
            if (paymentId == null ) {
                log.error("========================= PaymentController.completePayment() ERROR =========================");
                log.error("[MISSING_PAYMENT_ID] paymentId가 제공되지 않았습니다.");
                log.error("PortOne V2 결제 ID는 필수입니다.");
                log.error("=========================================================================================");
                return ResponseEntity.badRequest()
                        .body(createErrorResponse("MISSING_PAYMENT_ID", 
                                "paymentId is required for payment verification"));
            }
            
            /**
             * 입력 검증: merchant_uid 확인
             * - null 체크
             * - 빈 문자열 체크
             * merchant_uid가 없으면 우리 DB에서 Payment 조회 불가능
             */
            if (merchantUid == null || merchantUid.isEmpty()) {
                log.error("========================= PaymentController.completePayment() ERROR =========================");
                log.error("[MISSING_MERCHANT_UID] merchant_uid가 제공되지 않았습니다.");
                log.error("상점 주문 번호는 필수입니다.");
                log.error("=========================================================================================");
                return ResponseEntity.badRequest()
                        .body(createErrorResponse("MISSING_MERCHANT_UID", 
                                "merchant_uid is required for payment verification"));
            }
            
            log.info("입력 검증 완료 - paymentId와 merchant_uid가 모두 제공됨");
            log.info("PaymentVerificationService.verifyAndCompletePaymentV2() 호출 중...");
            
            /**
             * Step 3: 결제 검증 및 최종 확정
             * - PortOne V2 API에서 paymentId로 결제 정보 조회
             * - 우리 DB에서 merchant_uid로 Payment 정보 조회
             * - 금액 비교 검증
             * - 결제 상태 확인
             * - Payment 상태: PENDING → PAID
             * - StoreOrder 상태: PENDING → CONFIRMED
             */

            Payment payment = paymentVerificationService.verifyAndCompletePayment(paymentId, merchantUid);
            
            log.info("(=========================결제 검증 및 완료 성공!=========================");
            log.info("  - payment_id: {}", payment.getPaymentId());
            log.info("  - 결제 상태: {} → PAID", payment.getPaymentStatus());
            log.info("  - 결제 금액: {}", payment.getPaymentAmount());
            log.info("  - 결제 수단: {}", payment.getPaymentMethod());
            log.info("  - 결제 완료 시간: {}", payment.getPaymentPaidAt());
            log.info("  - 관련 주문 ID: {}", payment.getStoreOrder().getOrderId());
            log.info("=========================================================================================");
            
            /**
             * Step 4: 성공 응답 생성 및 반환
             * 클라이언트에 다음 정보 반환:
             * - success: true
             * - payment_id: 데이터베이스 결제 ID
             * - order_id: 관련 주문 ID
             * - payment_status: PAID (완료 상태)
             * - 결제 상세 정보들
             */

            PaymentCompleteResponseDTO response = 
                PaymentCompleteResponseDTO.builder()
                .success(true)
                .message("결제가 성공적으로 완료되었습니다.")
                .paymentId(payment.getPortOnePaymentId())
                .orderId(payment.getStoreOrder().getOrderId())
                .paymentStatus(payment.getPaymentStatus())
                .paymentAmount(payment.getPaymentAmount())
                .paymentMethod(payment.getPaymentMethod())
                .paymentPaidAt(payment.getPaymentPaidAt())
                .build();

            log.info("클라이언트에 성공 응답 반환:");
            log.info("  - success: true");
            log.info("  - payment_id: {}", payment.getPaymentId());
            log.info("  - order_id: {}", payment.getStoreOrder().getOrderId());
            log.info("========================= PaymentController.completePayment() SUCCESS =========================");
            
            return ResponseEntity.ok(response);
            
        } catch (PaymentException pe) {
            /**
             * PaymentException 처리
             * 비즈니스 로직 에러:
             * - AMOUNT_MISMATCH: PortOne V2에서 조회한 금액 ≠ DB 금액
             * - PAYMENT_NOT_FOUND: merchant_uid에 해당하는 Payment 없음
             * - ALREADY_PAID: 이미 결제된 주문
             * - PAYMENT_FAILED: PortOne V2에서 결제 실패 상태
             */
            log.error("========================= PaymentController.completePayment() ERROR =========================");
            log.error("[PaymentException] 결제 검증 실패");
            log.error("Error Code: {}", pe.getErrorCode());
            log.error("Error Message: {}", pe.getErrorMessage());
            log.error("발생 위치: PaymentVerificationService.verifyAndCompletePaymentV2()");
            log.error("요청 파라미터:");
            log.error("  - paymentId: {}", paymentId);
            log.error("  - merchant_uid: {}", merchantUid);
            log.error("=========================================================================================");
            
            PaymentCompleteResponseDTO errorResponse = 
                    PaymentCompleteResponseDTO.builder()
                    .success(false)
                    .message("결제 검증에 실패했습니다.")
                    .errorCode(pe.getErrorCode())
                    .errorMessage(pe.getErrorMessage())
                    .build();
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(errorResponse);
            
        } catch (Exception e) {

             /**
             * 예상치 못한 에러 처리
             * 가능한 원인:
             * - PortOne V2 API 서버 다운
             * - 네트워크 연결 실패
             * - 데이터베이스 연결 실패
             * - 예상치 못한 런타임 에러
             */

            log.error("========================= PaymentController.completePayment() ERROR =========================");
            log.error("[INTERNAL_ERROR] 예상치 못한 오류 발생");
            log.error("Exception Type: {}", e.getClass().getName());
            log.error("Error Message: {}", e.getMessage());
            log.error("발생 위치: PaymentController.completePayment() - 외부 API 호출 또는 DB 접근");
            log.error("요청 파라미터:");
            log.error("  - paymentId: {}", paymentId);
            log.error("  - merchant_uid: {}", merchantUid);
            log.error("=========================================================================================");
            log.error("Stack Trace: ", e);

            PaymentCompleteResponseDTO errorResponse = 
                PaymentCompleteResponseDTO.builder()
                .success(false)
                .message("결제 처리 중 오류가 발생했습니다.")
                .errorCode("INTERNAL_ERROR")
                .errorMessage("PaymentController - Internal server error - please contact support")
                .build();
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errorResponse);
        }
    }

    
    /**
     * 3️⃣ 결제 정보 조회 엔드포인트
     * GET /api/payments/{paymentId}
     * 
     * 완료된 결제의 상세 정보를 조회합니다.
     * 주문 상세 페이지에서 결제 정보를 표시할 때 사용합니다.
     * 
     * 사용 시나리오:
     * - 사용자가 "결제 영수증" 버튼을 클릭했을 때
     * - 관리자 페이지에서 결제 내역 조회
     * - 환불 프로세스 전에 결제 정보 확인
     * 
     * Response Example (200 OK):
     * {
     *   "success": true,
     *   "message": "결제 정보를 조회했습니다.",
     *   "payment_id": 456,
     *   "order_id": 2,
     *   "payment_status": "PAID",
     *   "payment_amount": 28000,
     *   "payment_method": "card",
     *   "payment_paid_at": "2024-01-14T12:30:00"
     * }
     * 
     * Response Example (404 Not Found):
     * {
     *   "success": false,
     *   "message": "오류가 발생했습니다.",
     *   "error_code": "PAYMENT_NOT_FOUND",
     *   "error_message": "결제 ID 456을 찾을 수 없습니다."
     * }
     * 
     * @param paymentId 조회할 결제의 ID
     * @return 결제 상세 정보
     */
    @GetMapping("/{paymentId}")
    public ResponseEntity<?> getPaymentDetails(@PathVariable Long paymentId) {
        try {
            log.info("========================= PaymentController.getPaymentDetails() START =========================");
            log.info("결제 정보 조회 요청 - payment_id: {}", paymentId);
            
            /**
             * 입력 검증: paymentId 유효성 확인
             */
            if (paymentId == null || paymentId <= 0) {
                log.error("========================= PaymentController.getPaymentDetails() ERROR =========================");
                log.error("[INVALID_PAYMENT_ID] 결제 ID가 유효하지 않습니다. 받은 값: {}", paymentId);
                log.error("결제 ID는 1 이상의 정수여야 합니다.");
                log.error("=========================================================================================");
                
                return ResponseEntity.badRequest()
                        .body(createErrorResponse("INVALID_PAYMENT_ID", 
                                "Payment ID must be a positive integer"));
            }
            
            log.info("PaymentService.getPaymentById() 호출 중...");
            
            /**
             * 데이터베이스에서 Payment 엔티티 조회
             */
            Payment payment = paymentService.getPaymentById(paymentId);
            
            log.info("결제 정보 조회 성공!");
            log.info("  - payment_id: {}", payment.getPaymentId());
            log.info("  - order_id: {}", payment.getStoreOrder().getOrderId());
            log.info("  - payment_status: {}", payment.getPaymentStatus());
            log.info("  - payment_amount: {}", payment.getPaymentAmount());
            log.info("  - payment_method: {}", payment.getPaymentMethod());
            
            /**
             * 성공 응답 생성
             */
            PaymentCompleteResponseDTO response = 
                    PaymentCompleteResponseDTO.builder()
                    .success(true)
                    .message("결제 정보를 조회했습니다.")
                    .paymentId(payment.getPortOnePaymentId())
                    .orderId(payment.getStoreOrder().getOrderId())
                    .paymentStatus(payment.getPaymentStatus())
                    .paymentAmount(payment.getPaymentAmount())
                    .paymentMethod(payment.getPaymentMethod())
                    .paymentPaidAt(payment.getPaymentPaidAt())
                    .build();
            
            log.info("========================= PaymentController.getPaymentDetails() SUCCESS =========================");
            
            return ResponseEntity.ok(response);
            
        } catch (PaymentException pe) {
            /**
             * 결제를 찾을 수 없는 경우
             */
            log.error("========================= PaymentController.getPaymentDetails() ERROR =========================");
            log.error("[PaymentException] 결제 조회 실패");
            log.error("Error Code: {}", pe.getErrorCode());
            log.error("Error Message: {}", pe.getErrorMessage());
            log.error("요청한 payment_id: {}", paymentId);
            log.error("발생 위치: PaymentService.getPaymentById()");
            log.error("=========================================================================================");
            
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse(pe.getErrorCode(), pe.getErrorMessage()));
        } catch (Exception e) {
            /**
             * 예상치 못한 에러 처리
             */
            log.error("========================= PaymentController.getPaymentDetails() ERROR =========================");
            log.error("[INTERNAL_ERROR] 예상치 못한 오류 발생");
            log.error("Exception Type: {}", e.getClass().getName());
            log.error("Error Message: {}", e.getMessage());
            log.error("요청한 payment_id: {}", paymentId);
            log.error("발생 위치: PaymentController.getPaymentDetails() - DB 접근");
            log.error("=========================================================================================");
            log.error("Stack Trace: ", e);
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("INTERNAL_ERROR", 
                            "An unexpected error occurred. Please contact support."));
        }
    }
    
    /**
     * 에러 응답 객체 생성 헬퍼 메서드
     * 
     * 모든 에러 응답을 일관된 형식으로 생성합니다.
     * 클라이언트가 errorCode와 errorMessage를 통해 에러 처리를 할 수 있습니다.
     * 
     * @param errorCode 에러 코드 (예: "INVALID_ORDER_ID", "AMOUNT_MISMATCH")
     * @param errorMessage 에러 상세 메시지 (사용자 친화적)
     * @return 에러 정보를 담은 PaymentCompleteResponseDTO
     */
    private PaymentCompleteResponseDTO createErrorResponse(String errorCode, String errorMessage) {
        return PaymentCompleteResponseDTO.builder()
                .success(false)
                .message("오류가 발생했습니다.")
                .errorCode(errorCode)
                .errorMessage(errorMessage)
                .build();
    }
}
