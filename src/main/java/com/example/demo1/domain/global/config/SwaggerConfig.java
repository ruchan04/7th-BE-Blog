package com.example.demo1.domain.global.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()

                .info(new Info()
                        .title("최유찬 Spring Board API 명세서")
                        .description("Swagger를 이용한 API 문서화 과제입니다.")
                        .version("1.0.0"));
    }
}