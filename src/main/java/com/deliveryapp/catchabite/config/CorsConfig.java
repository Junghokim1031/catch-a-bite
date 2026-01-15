package com.deliveryapp.catchabite.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * CorsConfig: React(프론트) ↔ Spring Boot(백엔드) 간 CORS 정책을 전역으로 정의
 *
 * Description:
 * - 브라우저는 "다른 Origin(도메인/포트)"으로 요청할 때 기본적으로 차단(CORS)합니다.
 * - React 개발 서버(localhost:3000 등)에서 API 서버(localhost:8080 등)로 호출하려면,
 *   백엔드가 허용 Origin/Method/Header를 명시적으로 응답해야 합니다. [web:32]
 *
 * 주의:
 * - allowCredentials(true)인 경우 allowedOrigins에 "*" 사용 불가 → 반드시 명시 Origin을 넣어야 함.
 */
@Configuration
public class CorsConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // ===== 허용 Origin 설정 =====
        // React 개발 서버 주소
        config.setAllowedOrigins(List.of(
            "http://localhost:3000"
        ));

        // ===== 인증 정보 포함 여부 =====
        // 쿠키(JSESSIONID) 기반 인증/세션을 쓸 경우 true가 필요할 수 있음.
        config.setAllowCredentials(true);

        // ===== 허용 HTTP Method =====
        // 브라우저 preflight(OPTIONS) 요청이 발생할 수 있으므로 OPTIONS 포함 권장. [web:32]
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));

        // ===== 허용 Header =====
        // 개발 단계에서는 "*"로 열어두고, 운영에서는 필요한 헤더만 명시하는 편이 안전합니다.
        config.setAllowedHeaders(List.of("*"));

        // ===== 클라이언트에서 읽을 수 있게 노출할 헤더(선택) =====
        // 예: 로그인 응답에서 Authorization 헤더를 내려주고 프론트에서 읽어야 하는 경우
        config.setExposedHeaders(List.of("Authorization"));

        // ===== URL 패턴별 CORS 적용 범위 =====
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }
}
