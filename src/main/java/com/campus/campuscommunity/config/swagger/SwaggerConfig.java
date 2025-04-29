package com.campus.campuscommunity.config.swagger;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.responses.ApiResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        // 개발 서버 설정
        Server devServer = new Server();
        devServer.setUrl("http://localhost:8080");
        devServer.setDescription("개발 서버");

        // 프로덕션 서버 설정 (나중에 배포 시 추가)
        Server prodServer = new Server();
        prodServer.setUrl("https://api.campuscommunity.com");
        prodServer.setDescription("운영 서버");

        SecurityScheme securityScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .description("JWT 토큰을 헤더에 입력하세요. 형식: Bearer {token}");

        return new OpenAPI()
                .info(new Info()
                        .title("Campus Community API")
                        .description("학과만 보이는 대학교 커뮤니티 서비스 API 문서")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Campus Community Team")
                                .email("support@campuscommunity.com")
                                .url("https://github.com/campuscommunity"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("http://www.apache.org/licenses/LICENSE-2.0.html"))
                        .termsOfService("https://campuscommunity.com/terms"))
                .servers(Arrays.asList(devServer, prodServer))
                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                .components(new Components()
                        .addSecuritySchemes("Bearer Authentication", securityScheme)
                        .addExamples("회원가입 요청 예시",
                                new Example()
                                        .value("{\n" +
                                                "  \"email\": \"user@university.ac.kr\",\n" +
                                                "  \"password\": \"password123\",\n" +
                                                "  \"name\": \"홍길동\",\n" +
                                                "  \"department\": \"컴퓨터공학과\"\n" +
                                                "}")
                                        .summary("회원가입 요청 예시"))
                        .addExamples("로그인 요청 예시",
                                new Example()
                                        .value("{\n" +
                                                "  \"email\": \"user@university.ac.kr\",\n" +
                                                "  \"password\": \"password123\"\n" +
                                                "}")
                                        .summary("로그인 요청 예시")));
    }
}