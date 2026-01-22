package com.deliveryapp.catchabite.config;

import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * CorsConfig: React(프론트) ↔ Spring Boot(백엔드) 간 CORS 정책을 전역으로 정의
 *
 * - allowCredentials(true)인 경우 allowedOrigins에 "*" 사용 불가 → 반드시 명시 Origin 필요
 */
@Log4j2 //Log4j2 추가
@Configuration
public class CorsConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOrigins(List.of(
                "http://localhost:3000",
                "http://localhost",
                "http://localhost:80",
                "http://127.0.0.1:3000",
                "http://127.0.0.1",
                "http://127.0.0.1:80",
                "http://localhost:5173",
                "http://localhost:5174",
                "http://127.0.0.1:5173",
                "http://127.0.0.1:5174"
        ));

        // 쿠키(JSESSIONID) 기반 인증/세션을 쓸 경우 true 필요
        config.setAllowCredentials(true);

        // preflight(OPTIONS) 포함 권장
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setExposedHeaders(List.of("Authorization"));

        log.info("✅ CORS Configuration loaded with allowed origins: {}", config.getAllowedOrigins());

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }
}
