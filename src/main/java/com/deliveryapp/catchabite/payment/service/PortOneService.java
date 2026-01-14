package com.deliveryapp.catchabite.payment.service;

import com.deliveryapp.catchabite.config.PortOneConfig;
import com.deliveryapp.catchabite.common.constant.PaymentConstant;
import com.deliveryapp.catchabite.common.exception.PaymentException;
import com.deliveryapp.catchabite.payment.dto.PortOnePaymentVerificationDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * PortOneService: PortOne API와의 통신을 담당하는 서비스 (V2)
 *
 * Description: PortOne V2 결제 조회/취소 API를 호출합니다.
 * V2에서는 토큰 발급 방식이 아닌 Authorization: PortOne {API_SECRET} 인증을 사용합니다.
 * 결제 ID는 payment_id(Long) 기반으로 동작합니다.
 *
 * Required Variables/Parameters:
 * - portOneConfig (PortOneConfig): PortOne API URL, V2_API_SECRET 설정
 * - restTemplate (RestTemplate): HTTP 클라이언트
 * - objectMapper (ObjectMapper): JSON 직렬화/역직렬화
 *
 * Output/Data Flow:
 * - Sends GET /payments/{paymentId} to PortOne V2 API
 * - Sends POST /payments/{paymentId}/cancel to PortOne V2 API
 * - Returns PortOnePaymentVerificationDTO
 *
 * Dependencies: PortOneConfig, RestTemplate, ObjectMapper
 */
@Log4j2
@Service
public class PortOneService {

    private final PortOneConfig portOneConfig;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public PortOneService(PortOneConfig portOneConfig,
                          RestTemplate restTemplate,
                          ObjectMapper objectMapper) {
        this.portOneConfig = portOneConfig;
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    /**
     * V2 공통 헤더 생성
     * - Authorization: PortOne {V2_API_SECRET}
     */
    private HttpHeaders buildAuthHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set(PaymentConstant.HEADER_CONTENT_TYPE, PaymentConstant.CONTENT_TYPE_JSON);
        headers.set(PaymentConstant.HEADER_AUTHORIZATION, "PortOne " + portOneConfig.getSecretKey());
        return headers;
    }

    /**
     * PortOne V2 결제 정보 조회
     * GET /payments/{paymentId}
     *
     * @param paymentId PortOne V2 payment_id (Long)
     * @return 결제 정보 DTO
     * @throws PaymentException API 호출 실패 시
     */
    public PortOnePaymentVerificationDTO getPaymentDetails(Long paymentId) {
        try {
            log.info("Fetching payment details from PortOne V2. paymentId: {}", paymentId);

            HttpEntity<Void> entity = new HttpEntity<>(buildAuthHeaders());

            String url = portOneConfig.getApiUrl()
                    + "/payments/" + paymentId;

            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            log.info("Raw JSON Response: {}", response.getBody());

            PortOnePaymentVerificationDTO paymentDetails =
                    objectMapper.readValue(response.getBody(), PortOnePaymentVerificationDTO.class);

            if (paymentDetails.getCode() != 0) {
                throw new PaymentException(
                        "PAYMENT_FETCH_FAILED",
                        "Failed to fetch payment details: " + paymentDetails.getMessage()
                );
            }

            log.info("Successfully fetched payment details from PortOne V2");
            return paymentDetails;

        } catch (PaymentException pe) {
            throw pe;
        } catch (RestClientException e) {
            log.error("RestTemplate error while fetching payment details (V2)", e);
            throw new PaymentException(
                    "PORTONE_API_ERROR",
                    PaymentConstant.ERROR_PORTONE_API_FAILED,
                    e
            );
        } catch (Exception e) {
            log.error("Error while fetching payment details (V2)", e);
            throw new PaymentException(
                    "PAYMENT_PROCESSING_ERROR",
                    "Error processing PortOne payment response",
                    e
            );
        }
    }

    /**
     * PortOne V2 결제 취소
     * POST /payments/{paymentId}/cancel
     *
     * @param paymentId PortOne V2 payment_id (Long)
     * @param cancelReason 취소 사유
     * @return 취소 결과 DTO
     * @throws PaymentException API 호출 실패 시
     */
    public PortOnePaymentVerificationDTO cancelPayment(Long paymentId, String cancelReason) {
        try {
            log.info("Cancelling payment in PortOne V2. paymentId: {}", paymentId);

            HttpHeaders headers = buildAuthHeaders();

            // V2 취소 요청 바디
            String requestBody = String.format(
                    "{\"reason\":\"%s\"}",
                    (cancelReason != null && !cancelReason.isBlank()) ? cancelReason : "User requested"
            );

            HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

            String url = portOneConfig.getApiUrl()
                    + "/payments/" + paymentId + "/cancel";

            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            log.info("Raw JSON Response (cancel): {}", response.getBody());

            PortOnePaymentVerificationDTO cancelResult =
                    objectMapper.readValue(response.getBody(), PortOnePaymentVerificationDTO.class);

            if (cancelResult.getCode() != 0) {
                throw new PaymentException(
                        "PAYMENT_CANCEL_FAILED",
                        "Failed to cancel payment: " + cancelResult.getMessage()
                );
            }

            log.info("Successfully cancelled payment in PortOne V2");
            return cancelResult;

        } catch (PaymentException pe) {
            throw pe;
        } catch (RestClientException e) {
            log.error("RestTemplate error while cancelling payment (V2)", e);
            throw new PaymentException(
                    "PORTONE_API_ERROR",
                    PaymentConstant.ERROR_PORTONE_API_FAILED,
                    e
            );
        } catch (Exception e) {
            log.error("Error while cancelling payment (V2)", e);
            throw new PaymentException(
                    "CANCEL_PROCESSING_ERROR",
                    "Error processing PortOne cancel response",
                    e
            );
        }
    }
}
