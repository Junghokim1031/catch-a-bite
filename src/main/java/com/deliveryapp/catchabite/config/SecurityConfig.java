package com.deliveryapp.catchabite.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
// import org.springframework.security.config.Customizer;
/**
 * Spring Security 기본 설정 클래스
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // 보안 필터 체인 설정
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 인증 API 경로는 CSRF 예외 처리
            .csrf(csrf -> csrf.ignoringRequestMatchers("/api/v1/auth/**"))
            // 인증/회원 관련 경로 접근 허용
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/index.html", "/auth/**", "/api/v1/auth/**").permitAll()
                .anyRequest().permitAll()
            )
            // 기본 폼 로그인 및 HTTP Basic 비활성화
            .formLogin(form -> form.disable())
            .httpBasic(basic -> basic.disable());

        return http.build();
    }
    /**
     * HTTP Security 설정 (Lambda DSL 문법)
     * 
     * @param http HttpSecurity 객체
     * @return 설정된 SecurityFilterChain
     * @throws Exception 설정 예외
     */
    // @Bean
    // public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    //     http
    //         // ========== 인증 허용/차단 설정 ==========
    //         .authorizeHttpRequests(authz -> authz
    //             // ✅ 결제 API 전체 허용
    //             .requestMatchers("/api/payments/**").permitAll()
                
    //             // ✅ 인증 관련 API 허용
    //             .requestMatchers("/api/auth/**").permitAll()
    //             .requestMatchers("/api/users/signup").permitAll()
    //             .requestMatchers("/api/users/login").permitAll()
                
    //             // ✅ 공개 리소스 허용
    //             .requestMatchers("/public/**").permitAll()
    //             .requestMatchers("/static/**").permitAll()
                
    //             // ✅ 헬스 체크 허용
    //             .requestMatchers("/health").permitAll()
    //             .requestMatchers("/actuator/**").permitAll()
                
    //             // ❌ 나머지는 모두 인증 필수
    //             .anyRequest().authenticated()
    //         )
            
    //         // ========== 폼 로그인 설정 ==========
    //         .formLogin(form -> form
    //             .loginPage("/login")
    //             .permitAll()
    //             .defaultSuccessUrl("/dashboard", true)
    //             .failureUrl("/login?error=true")
    //         )
            
    //         // ========== 로그아웃 설정 ==========
    //         .logout(logout -> logout
    //             .logoutUrl("/logout")
    //             .logoutSuccessUrl("/login?logout=true")
    //             .permitAll()
    //         )
            
    //         // ========== CSRF 설정 ==========
    //         .csrf(csrf -> csrf
    //             .disable()  // ⚠️ 개발/테스트 환경에서만 비활성화
    //         )
            
    //         // ========== HTTP Basic 인증 ==========
    //         .httpBasic(Customizer.withDefaults());
        
    //     return http.build();
    // }
}
