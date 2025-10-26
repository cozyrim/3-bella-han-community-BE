package com.ktbweek4.community.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${app.upload.dir}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // http://localhost:8080/files/abc.jpg  →  {uploadDir}/abc.jpg
        registry.addResourceHandler("/files/**")
                .addResourceLocations("file:" + uploadDir + "/")

                .setCachePeriod(3600); // 선택: 캐시 1시간
    }
}