package com.campus.campuscommunity.global.config.oauth;
import org.springframework.beans.factory.annotation.Value;
import com.campus.campuscommunity.global.config.jwt.JwtTokenProvider;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;

    // 프론트엔드 리다이렉트 URL (실제 환경에 맞게 설정 필요)
    @Value("${oauth2.redirect-uri:http://localhost:3000/login-success,http://localhost:8080/login-success}")
    private String redirectUri;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = (String) oAuth2User.getAttributes().get("email");

        log.info("OAuth2 로그인 성공, 이메일: {}", email);
        log.info("리다이렉트 URI: {}", redirectUri);

        String token = jwtTokenProvider.createToken(email);

        String[] allowedUris = redirectUri.split(",");
        String targetUrl = null;

        for (String uri : allowedUris) {
            try {
                targetUrl = UriComponentsBuilder.fromUriString(uri)
                        .queryParam("token", token)
                        .build().toUriString();

                log.info("시도 중인 URI: {}", uri);

                // URL 연결 테스트
                URL url = new URL(uri);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(3000);
                connection.connect();

                // 연결 성공하면 해당 URL로 리다이렉트
                log.info("연결 성공한 URL: {}", uri);
                response.sendRedirect(targetUrl);
                return;
            } catch (Exception e) {
                log.warn("URI 연결 실패: {}, 오류: {}", uri, e.getMessage());
            }
        }

        // 모든 URI 연결 실패 시 예외 처리
        if (targetUrl == null) {
            log.error("모든 리다이렉트 URI 연결 실패");
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "리다이렉트 URI 연결 실패");
        }
    }
}
