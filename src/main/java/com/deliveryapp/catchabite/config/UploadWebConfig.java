package com.deliveryapp.catchabite.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 로컬 파일 업로드 서빙 설정
 * - ...
 */
@Configuration
public class UploadWebConfig implements WebMvcConfigurer {

    /**
     * application.properties 기준(현재 main.zip): com.deliveryapp.catchabite=C:\\upload
     */
    @Value("${com.deliveryapp.catchabite}")
    private String uploadRoot;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path root = Paths.get(uploadRoot, "uploads");
        String location = root.toUri().toString(); // file:///...

        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(location);
    }
}
