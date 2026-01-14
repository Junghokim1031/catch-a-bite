package com.deliveryapp.catchabite.payment.converter;

import com.deliveryapp.catchabite.payment.dto.PortOnePaymentRequestDTO;
import com.deliveryapp.catchabite.payment.dto.PortOnePaymentResponseDTO;
import com.deliveryapp.catchabite.common.constant.PaymentConstant;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

/**
 * PaymentDTOConverter: Payment 관련 DTO 변환 클래스
 * 
 * Description: DTO 간 변환 및 엔티티-DTO 변환을 담당합니다.
 * PortOne 응답 DTO와 Payment 요청/응답 DTO를 변환합니다.
 * 
 * Required Variables/Parameters:
 * - paymentRequestDTO (PortOnePaymentRequestDTO)
 * - merchantUid (String)
 * - impKey (String)
 * 
 * Output/Data Flow:
 * - Used by PaymentService for DTO conversion
 * - Creates PortOnePaymentResponseDTO for frontend
 * 
 * Dependencies: PaymentConstant, Lombok
 */

@Log4j2
@Component
public class PaymentDTOConverter {
    
    /**
     * PortOnePaymentRequestDTO → PortOnePaymentResponseDTO 변환
     * 프론트에서 받은 결제 준비 요청을 PortOne API 응답 형식으로 변환합니다.
     * 
     * @param merchantUid 생성된 merchant_uid
     * @param impKey PortOne Imp Key
     * @param request 프론트에서 받은 결제 요청
     * @param timestamp 준비 완료 시간
     * @return PortOne 결제 창에 필요한 응답
     */
    public PortOnePaymentResponseDTO toPortOnePaymentResponseDTO(
            String merchantUid,
            String impKey,
            PortOnePaymentRequestDTO request,
            Long timestamp) {
        
        return PortOnePaymentResponseDTO.builder()
                .merchantUid(merchantUid)
                .impKey(impKey)
                .paymentAmount(request.getPaymentAmount())
                .buyerName(request.getBuyerName())
                .buyerEmail(request.getBuyerEmail())
                .buyerTel(request.getBuyerTel())
                .preparedAt(timestamp)
                .apiEndpoint(PaymentConstant.PORTONE_API_BASE_URL)
                .build();
    }
}
