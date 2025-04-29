package com.campus.campuscommunity.config.oauth;

import com.campus.campuscommunity.domain.user.entity.User;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
public class OAuthAttributes {
    private Map<String, Object> attributes;
    private String nameAttributeKey;
    private String name;
    private String email;

    @Builder
    public OAuthAttributes(Map<String, Object> attributes, String nameAttributeKey, String name, String email) {
        this.attributes = attributes;
        this.nameAttributeKey = nameAttributeKey;
        this.name = name;
        this.email = email;
    }

    // OAuth2User에서 반환하는 사용자 정보는 Map이므로 값을 변환해야 함
    public static OAuthAttributes of(String registrationId, String userNameAttributeName, Map<String, Object> attributes) {
        // 구글 로그인인 경우
        return ofGoogle(userNameAttributeName, attributes);
    }

    private static OAuthAttributes ofGoogle(String userNameAttributeName, Map<String, Object> attributes) {
        return OAuthAttributes.builder()
                .name((String) attributes.get("name"))
                .email((String) attributes.get("email"))
                .attributes(attributes)
                .nameAttributeKey(userNameAttributeName)
                .build();
    }

    // User 엔티티 생성 (소셜 로그인 시 기본값으로 생성)
    public User toEntity() {
        return User.builder()
                .name(name)
                .email(email)
                .password("OAUTH2_USER") // 임시 비밀번호 설정 (null 대신)
                .department("미설정") // 기본값 설정
                .verified(false)
                .role(User.Role.USER)
                .providerType(User.ProviderType.GOOGLE)
                .providerId(email) // 이메일을 제공자 ID로 사용
                .build();
    }
}