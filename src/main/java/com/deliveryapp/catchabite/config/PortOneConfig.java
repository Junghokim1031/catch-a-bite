package com.deliveryapp.catchabite.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import lombok.Getter;

/**
 * PortOneConfig: PortOne 설정 클래스
 * 
 * Description: application.properties에서 PortOne 자격증명을 읽어 Bean으로 제공합니다.
 * 싱글톤으로 관리되어 모든 서비스에서 주입 가능합니다.
 * 
 * Required Variables/Parameters:
 * - storeId: PortOne 상점 ID (application.properties에서 읽음)
 * - secretKey: PortOne 시크릿 키 (application.properties에서 읽음)
 * 
 * Output/Data Flow:
 * - Injected into PortOneService
 * 
 * Dependencies: Spring Framework, Lombok
 */

@Configuration
@Getter
public class PortOneConfig {
    
    /**
     * PortOne 상점 ID
     * application.properties의 portone.store-id에서 읽음
     */
    @Value("${portone.store-id}")
    private String storeId;
    
    /**
     * PortOne 시크릿 키
     * application.properties의 portone.secret-key에서 읽음
     */
    @Value("${portone.secret-key}")
    private String secretKey;
    
    /**
     * PortOne API 기본 URL
     * 기본값: https://api.iamport.kr
     */
    @Value("${portone.baseUrl}")
    private String apiUrl;
}
