package com.telecom.cqrs.query.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger 문서화를 위한 설정 클래스입니다.
 */
@Configuration
public class SwaggerConfig {

    /**
     * Swagger API 문서 정보를 설정합니다.
     */
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("통신사 요금제 조회/사용량 API")
                        .description("CQRS 패턴을 적용한 요금제 관리 시스템의 API 문서입니다.")
                        .version("1.0.0"));
    }
}
