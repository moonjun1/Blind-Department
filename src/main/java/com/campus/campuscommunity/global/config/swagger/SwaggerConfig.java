package com.campus.campuscommunity.global.config.swagger;

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
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;

@Configuration
public class SwaggerConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("*")
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*")
                        .maxAge(3600);
            }
        };
    }

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
                        // 사용자 API 예시
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
                                        .summary("로그인 요청 예시"))
                        // 게시판 API 예시
                        .addExamples("게시글 작성 요청 예시",
                                new Example()
                                        .value("{\n" +
                                                "  \"title\": \"첫 번째 게시글입니다\",\n" +
                                                "  \"content\": \"안녕하세요, 블라인드 커뮤니티 기능 테스트 중입니다. 학과별로 익명성이 보장되는지 확인해봅시다.\",\n" +
                                                "  \"category\": \"FREE\"\n" +
                                                "}")
                                        .summary("게시글 작성 요청 예시"))
                        .addExamples("게시글 수정 요청 예시",
                                new Example()
                                        .value("{\n" +
                                                "  \"title\": \"수정된 게시글 제목\",\n" +
                                                "  \"content\": \"게시글 내용을 수정했습니다. 더 자세한 내용을 추가했습니다.\",\n" +
                                                "  \"category\": \"QNA\"\n" +
                                                "}")
                                        .summary("게시글 수정 요청 예시"))
                        .addExamples("게시글 목록 응답 예시",
                                new Example()
                                        .value("{\n" +
                                                "  \"status\": 200,\n" +
                                                "  \"message\": \"요청이 성공적으로 처리되었습니다.\",\n" +
                                                "  \"data\": {\n" +
                                                "    \"boards\": [\n" +
                                                "      {\n" +
                                                "        \"id\": 1,\n" +
                                                "        \"title\": \"첫 번째 게시글입니다\",\n" +
                                                "        \"writerDepartment\": \"컴퓨터공학과\",\n" +
                                                "        \"category\": \"FREE\",\n" +
                                                "        \"categoryDisplayName\": \"자유게시판\",\n" +
                                                "        \"viewCount\": 42,\n" +
                                                "        \"likeCount\": 15,\n" +
                                                "        \"commentCount\": 7,\n" +
                                                "        \"createdAt\": \"2025-04-15T14:30:00\"\n" +
                                                "      },\n" +
                                                "      {\n" +
                                                "        \"id\": 2,\n" +
                                                "        \"title\": \"스터디 모집합니다\",\n" +
                                                "        \"writerDepartment\": \"전자공학과\",\n" +
                                                "        \"category\": \"STUDY\",\n" +
                                                "        \"categoryDisplayName\": \"스터디\",\n" +
                                                "        \"viewCount\": 18,\n" +
                                                "        \"likeCount\": 5,\n" +
                                                "        \"commentCount\": 2,\n" +
                                                "        \"createdAt\": \"2025-04-15T11:20:00\"\n" +
                                                "      }\n" +
                                                "    ],\n" +
                                                "    \"totalPages\": 5,\n" +
                                                "    \"totalElements\": 42,\n" +
                                                "    \"currentPage\": 0\n" +
                                                "  }\n" +
                                                "}")
                                        .summary("게시글 목록 응답 예시"))
                        .addExamples("게시글 상세 응답 예시",
                                new Example()
                                        .value("{\n" +
                                                "  \"status\": 200,\n" +
                                                "  \"message\": \"요청이 성공적으로 처리되었습니다.\",\n" +
                                                "  \"data\": {\n" +
                                                "    \"id\": 1,\n" +
                                                "    \"title\": \"첫 번째 게시글입니다\",\n" +
                                                "    \"content\": \"안녕하세요, 블라인드 커뮤니티 기능 테스트 중입니다. 학과별로 익명성이 보장되는지 확인해봅시다.\",\n" +
                                                "    \"writerDepartment\": \"컴퓨터공학과\",\n" +
                                                "    \"writerId\": 1,\n" +
                                                "    \"category\": \"FREE\",\n" +
                                                "    \"categoryDisplayName\": \"자유게시판\",\n" +
                                                "    \"viewCount\": 43,\n" +
                                                "    \"likeCount\": 15,\n" +
                                                "    \"dislikeCount\": 3,\n" +
                                                "    \"createdAt\": \"2025-04-15T14:30:00\",\n" +
                                                "    \"updatedAt\": \"2025-04-15T15:45:00\",\n" +
                                                "    \"likeStatus\": \"LIKE\"\n" +
                                                "  }\n" +
                                                "}")
                                        .summary("게시글 상세 응답 예시"))
                );
    }
}