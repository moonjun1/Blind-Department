package com.campus.campuscommunity.config.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component // 스프링 컴포넌트로 등록
@RequiredArgsConstructor // final 필드에 대한 생성자를 자동 생성
public class JwtTokenProvider {

    // JWT 비밀키 (실제 프로젝트에서는 환경변수나 설정 파일에서 가져오는 것이 좋습니다)
    private String secretKey = "campus-community-secret-key-for-jwt-token-should-be-longer-than-256-bits";

    // 토큰 유효시간 (1시간)
    private final long tokenValidMillisecond = 1000L * 60 * 60;

    private final UserDetailsService userDetailsService;

    private Key key;

    // 객체 초기화: secretKey를 Base64로 인코딩
    @PostConstruct
    protected void init() {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    // JWT 토큰 생성
    public String createToken(String userPk) {
        Claims claims = Jwts.claims().setSubject(userPk); // JWT payload에 저장되는 정보
        Date now = new Date();

        return Jwts.builder()
                .setClaims(claims) // 정보 저장
                .setIssuedAt(now) // 토큰 발행 시간
                .setExpiration(new Date(now.getTime() + tokenValidMillisecond)) // 토큰 만료 시간
                .signWith(key, SignatureAlgorithm.HS256) // 암호화 알고리즘, 비밀키
                .compact();
    }

    // JWT 토큰에서 인증 정보 조회
    public Authentication getAuthentication(String token) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(this.getUserPk(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    // 토큰에서 회원 구별 정보(PK, 여기서는 이메일) 추출
    public String getUserPk(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().getSubject();
    }

    // Request의 Header에서 token 값 추출
    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    // 토큰의 유효성 + 만료일자 확인
    public boolean validateToken(String jwtToken) {
        try {
            Jws<Claims> claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(jwtToken);
            return !claims.getBody().getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }
}