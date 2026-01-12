package com.deliveryapp.catchabite.auth.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public DaoAuthenticationProvider authenticationProvider(CustomUserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
		provider.setUserDetailsService(userDetailsService);
		provider.setPasswordEncoder(passwordEncoder);
		return provider;
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

		http
			.authorizeHttpRequests(auth -> auth
				.requestMatchers("/", "/auth/**", "/api/auth/**", "/css/**", "/js/**", "/images/**", "/error").permitAll()
				.requestMatchers("/user/**").hasRole("USER")
				.requestMatchers("/owner/**").hasRole("OWNER")
				.requestMatchers("/rider/**").hasRole("RIDER")
				.anyRequest().authenticated()
			)
            .formLogin(form -> form
                .loginPage("/auth/login")        // 우리가 만들 로그인 페이지
                .loginProcessingUrl("/login")    // Security가 처리하는 POST URL(고정)
                .successHandler(new RoleBasedLoginSuccessHandler())
                .permitAll()
                
            )
            
			.logout(logout -> logout.permitAll());

		// CSRF 기본 유지 (테스트로 POST 때 403 나는 게 정상)
		return http.build();
	}
}
