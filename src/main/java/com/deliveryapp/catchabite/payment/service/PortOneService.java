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

    private HttpHeaders buildAuthHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set(PaymentConstant.HEADER_CONTENT_TYPE, PaymentConstant.CONTENT_TYPE_JSON);
        headers.set(PaymentConstant.HEADER_AUTHORIZATION, "PortOne " + portOneConfig.getSecretKey());
        return headers;
    }

    public PortOnePaymentVerificationDTO getPaymentDetails(String paymentId) {
        try {
            log.info("Fetching payment details from PortOne V2. paymentId: {}", paymentId);

            HttpEntity<Void> entity = new HttpEntity<>(buildAuthHeaders());
            String url = portOneConfig.getApiUrl() + "/payments/" + paymentId;

            ResponseEntity<String> response = restTemplate.exchange(
                    url, HttpMethod.GET, entity, String.class
            );

            log.info("Raw JSON Response: {}", response.getBody());

            // V2: Directly map the response to DTO (No wrapper check needed)
            return objectMapper.readValue(response.getBody(), PortOnePaymentVerificationDTO.class);

        } catch (RestClientException e) {
            log.error("RestTemplate error (V2)", e);
            throw new PaymentException("PORTONE_API_ERROR", "Failed to communicate with PortOne API", e);
        } catch (Exception e) {
            log.error("Error fetching payment details", e);
            throw new PaymentException("PAYMENT_PROCESSING_ERROR", "Error parsing PortOne response", e);
        }
    }

    public PortOnePaymentVerificationDTO cancelPayment(String paymentId, String cancelReason) {
        try {
            log.info("Cancelling payment in PortOne V2. paymentId: {}", paymentId);
            HttpHeaders headers = buildAuthHeaders();
            String requestBody = String.format("{\"reason\":\"%s\"}", 
                (cancelReason != null && !cancelReason.isBlank()) ? cancelReason : "User requested");

            HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
            String url = portOneConfig.getApiUrl() + "/payments/" + paymentId + "/cancel";

            ResponseEntity<String> response = restTemplate.exchange(
                    url, HttpMethod.POST, entity, String.class
            );

            return objectMapper.readValue(response.getBody(), PortOnePaymentVerificationDTO.class);

        } catch (Exception e) {
            log.error("Error cancelling payment", e);
            throw new PaymentException("CANCEL_PROCESSING_ERROR", "Error cancelling payment", e);
        }
    }
}