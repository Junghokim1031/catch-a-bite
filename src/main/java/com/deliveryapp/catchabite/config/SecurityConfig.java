package com.deliveryapp.catchabite.config;

import com.deliveryapp.catchabite.security.RestAccessDeniedHandler;
import com.deliveryapp.catchabite.security.RestAuthenticationEntryPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            RestAuthenticationEntryPoint authenticationEntryPoint,
            RestAccessDeniedHandler accessDeniedHandler
    ) throws Exception {

        http
            .cors(Customizer.withDefaults())

            // ✅ 결제 API만 CSRF 예외
            .csrf(csrf -> csrf
            // ✅ API는 CSRF 예외 (MockMvc 테스트도 이걸 전제로 작성됨)
            .ignoringRequestMatchers("/api/**")

            // ✅ 결제 API도 포함(명시해도 되고 없어도 됨: /api/**에 포함되면 중복)
            .ignoringRequestMatchers("/api/payments/**")
        )

            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))

            .exceptionHandling(ex -> ex
                    .authenticationEntryPoint(authenticationEntryPoint)
                    .accessDeniedHandler(accessDeniedHandler)
            )

            // ✅ authorizeHttpRequests는 반드시 1번만
            .authorizeHttpRequests(auth -> auth
                    // Preflight
                    .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                    // 인증 관련
                    .requestMatchers("/api/v1/auth/login").permitAll()
                    .requestMatchers("/api/v1/auth/signup").permitAll()
                    .requestMatchers("/api/v1/auth/exists/**").permitAll()
                    .requestMatchers("/api/v1/deliverer/auth/**").permitAll()
                    .requestMatchers("/api/v1/store-owner/auth/**").permitAll()

                    // ✅ 결제 API 공개
                    .requestMatchers("/api/payments/**").permitAll()

                    // 역할 기반(프로젝트 실제 RolePrefix에 맞춰 조정 필요)
                    .requestMatchers("/api/v1/user/profile").hasRole("USER")
                    .requestMatchers("/api/v1/user/**").authenticated()
                    .requestMatchers("/api/v1/rider/**").hasRole("RIDER")
                    .requestMatchers("/api/v1/owner/**").hasRole("STORE_OWNER")

                    // 나머지 API
                    .requestMatchers("/api/v1/**").authenticated()

                    .anyRequest().permitAll()
            )

            .formLogin(form -> form.disable())
            .httpBasic(basic -> basic.disable());

        return http.build();
    }
}
