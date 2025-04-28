package com.campus.campuscommunity.config.security;

import com.campus.campuscommunity.config.jwt.JwtAuthenticationFilter;
import com.campus.campuscommunity.config.jwt.JwtTokenProvider;
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
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                // CSRF 보호 비활성화 (REST API에서는 일반적으로 필요 없음)
                .csrf(AbstractHttpConfigurer::disable)
                // 세션 관리 설정 (JWT 사용하므로 세션은 STATELESS로 설정)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // HTTP 기본 인증 비활성화
                .httpBasic(AbstractHttpConfigurer::disable)
                // 요청에 대한 인가 설정
                .authorizeHttpRequests(auth -> auth
                        // 회원가입, 로그인은 누구나 접근 가능
                        .requestMatchers("/api/users/signup", "/api/users/login").permitAll()
                        // 그 외 모든 요청은 인증 필요
                        .anyRequest().authenticated()
                )
                // JWT 필터 추가
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider),
                        UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}