package com.deliveryapp.catchabite.config;

import com.deliveryapp.catchabite.config.interceptor.OwnerAuthInterceptor;
import com.deliveryapp.catchabite.config.interceptor.RiderAuthInterceptor;
import com.deliveryapp.catchabite.config.interceptor.UserAuthInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/** 웹 인터셉터 등록 설정 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        String[] excludes = {
            "/auth/**",
            "/api/**",
            "/css/**",
            "/js/**",
            "/images/**",
            "/webjars/**",
            "/favicon.ico",
            "/error"
        };

        registry.addInterceptor(new UserAuthInterceptor())
            .addPathPatterns("/user/**")
            .excludePathPatterns(excludes);

        registry.addInterceptor(new OwnerAuthInterceptor())
            .addPathPatterns("/owner/**")
            .excludePathPatterns(excludes);

        registry.addInterceptor(new RiderAuthInterceptor())
            .addPathPatterns("/rider/**")
            .excludePathPatterns(excludes);
    }
}
