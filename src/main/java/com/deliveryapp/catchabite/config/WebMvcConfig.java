package com.deliveryapp.catchabite.config;

import com.deliveryapp.catchabite.config.interceptor.RiderAuthInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/** 웹 인터셉터 등록 설정 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new RiderAuthInterceptor())
            .addPathPatterns("/rider/**")
            .excludePathPatterns(
                "/auth/**",
                "/api/**",
                "/css/**",
                "/js/**",
                "/images/**",
                "/webjars/**",
                "/favicon.ico",
                "/error"
            );
    }
}
