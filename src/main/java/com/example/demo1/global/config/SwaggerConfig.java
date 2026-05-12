package com.example.demo1.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        String securityJwtName = "JWT_Auth"; // 보안 스키마 이름
        SecurityRequirement securityRequirement = new SecurityRequirement().addList(securityJwtName);

        Components components = new Components().addSecuritySchemes(securityJwtName,
                new SecurityScheme()
                        .name(securityJwtName)
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")); // Bearer 형식을 사용하겠다고 선언

        return new OpenAPI()
                .info(new Info()
                        .title("최유찬 Spring Board API 명세서")
                        .version("1.0.0")
                        .description("Swagger를 이용한 API 문서화 과제입니다."))
                .addSecurityItem(securityRequirement)
                .components(components);
    }
}