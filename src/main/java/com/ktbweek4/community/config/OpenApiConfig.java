package com.ktbweek4.community.config;

import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI defaultOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Community API")
                        .version("v1")
                        .description("커뮤니티 프로젝트 API 문서"));
    }
}