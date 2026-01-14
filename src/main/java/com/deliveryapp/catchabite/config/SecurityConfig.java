package com.deliveryapp.catchabite.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * SecurityConfig: 애플리케이션 전역 Spring Security 정책 설정
 *
 * - JWT 없이 운영/개발 가능한 기본 설정
 * - React 호출을 위해 CORS + OPTIONS(preflight) 허용
 * - /api/v1/auth/** 는 CSRF 예외 처리 (로그인/회원가입 API 편의)
 * - 현재는 전체 permitAll (서버 부팅/연동 안정 우선)
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            // ===== CORS =====
            // (CorsConfigurationSource Bean이 있으면 그 설정을 사용)
            .cors(Customizer.withDefaults())

            // ===== CSRF =====
            // JWT 없이도, API 기반이면 보통 disable을 많이 쓰지만
            // 기존 스타일 유지: 인증 API만 CSRF 예외 처리
              .csrf(csrf -> csrf.ignoringRequestMatchers(
                "/api/v1/auth/**",
                "/api/payments/**" 
            ))

            // ===== 인가(Authorization) =====
            .authorizeHttpRequests(auth -> auth
                // preflight 요청은 무조건 허용
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                // 정적/페이지 및 인증 API 허용
                .requestMatchers("/", "/index.html", "/auth/**", "/api/v1/auth/**").permitAll()

                // 개발단계: 나머지도 전부 허용
                .anyRequest().permitAll()

                // 보호가 필요해지면 아래처럼 변경
                // .anyRequest().authenticated()
            )

            // ===== 인증 방식 비활성화 =====
            // 폼 로그인 사용 안 함
            .formLogin(form -> form.disable())

            // Basic 인증 사용 안 함
            .httpBasic(basic -> basic.disable());

        return http.build();
    }
}
