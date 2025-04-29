package com.campus.campuscommunity.config.security;

import com.campus.campuscommunity.config.jwt.JwtAuthenticationFilter;
import com.campus.campuscommunity.config.jwt.JwtTokenProvider;
import com.campus.campuscommunity.config.oauth.CustomOAuth2UserService;
import com.campus.campuscommunity.config.oauth.OAuth2SuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration // 스프링 설정 클래스임을 나타냅니다
@EnableWebSecurity // 웹 보안 활성화
@RequiredArgsConstructor // final 필드에 대한 생성자를 자동 생성합니다
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;

    // 비밀번호 인코더 빈 등록
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 보안 필터 체인 설정
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, CustomOAuth2UserService customOAuth2UserService, OAuth2SuccessHandler oAuth2SuccessHandler) throws Exception {
        return http
                // CSRF 보호 비활성화 (REST API에서는 일반적으로 필요 없음)
                .csrf(AbstractHttpConfigurer::disable)
                // 세션 관리 설정 (JWT 사용하므로 세션은 STATELESS로 설정)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // HTTP 기본 인증 비활성화
                .httpBasic(AbstractHttpConfigurer::disable)
                // 요청에 대한 인가 설정
                .authorizeHttpRequests(auth -> auth
                        // Swagger UI 관련 경로 허용
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**",
                                "/api-docs/**",
                                "/swagger-resources/**",
                                "/webjars/**",
                                "/login/**", // OAuth2 로그인 URL 추가
                                "/oauth2/**",// OAuth2 콜백 URL 추가
                                "/", // 루트 경로 허용
                                "/login-page" // 로그인 페이지 경로 허용

                        )
                        .permitAll()
                        // 회원가입, 로그인은 누구나 접근 가능
                        .requestMatchers("/api/users/signup", "/api/users/login").permitAll()
                        // 학생증 인증은 인증된 사용자만 가능
                        .requestMatchers("/api/users/verify-department/ocr").authenticated()
                        // 그 외 모든 요청은 인증 필요
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService)
                        )
                        .successHandler(oAuth2SuccessHandler)

                )
                // JWT 필터 추가
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider),
                        UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}