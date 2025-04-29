package com.campus.campuscommunity.config.oauth;

import com.campus.campuscommunity.config.jwt.JwtTokenProvider;
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

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;

    // 프론트엔드 리다이렉트 URL (실제 환경에 맞게 설정 필요)
    private static final String REDIRECT_URI = "http://localhost:3000/oauth2/redirect";

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = (String) oAuth2User.getAttributes().get("email");

        log.info("OAuth2 로그인 성공, 이메일: {}", email);
        log.info("속성: {}", oAuth2User.getAttributes());

        // JWT 토큰 생성
        String token = jwtTokenProvider.createToken(email);

        // 리다이렉트 URL 생성 (토큰 포함)
        String targetUrl = UriComponentsBuilder.fromUriString(REDIRECT_URI)
                .queryParam("token", token)
                .build().toUriString();

        // 리다이렉트
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}