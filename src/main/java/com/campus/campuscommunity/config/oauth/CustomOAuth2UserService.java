package com.campus.campuscommunity.config.oauth;

import com.campus.campuscommunity.domain.user.entity.User;
import com.campus.campuscommunity.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        // 현재 로그인 진행 중인 서비스 구분 (구글, 네이버 등)
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        // OAuth2 로그인 시 키가 되는 필드 값 (PK)
        String userNameAttributeName = userRequest.getClientRegistration()
                .getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();

        // OAuth2UserService를 통해 가져온 데이터를 담을 클래스
        OAuthAttributes attributes = OAuthAttributes.of(
                registrationId, userNameAttributeName, oAuth2User.getAttributes());

        User user = saveOrUpdate(attributes);
        log.info("OAuth2 로그인 성공: {}({})", user.getName(), user.getEmail());

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(user.getRoleKey())),
                attributes.getAttributes(),
                attributes.getNameAttributeKey());
    }

    // 사용자 정보 저장 또는 업데이트
    private User saveOrUpdate(OAuthAttributes attributes) {
        Optional<User> userOptional = userRepository.findByEmail(attributes.getEmail());

        User user;
        if (userOptional.isPresent()) {
            // 이미 가입된 사용자면 이름만 업데이트
            user = userOptional.get();
            user = User.builder()
                    .id(user.getId())
                    .name(attributes.getName())
                    .email(user.getEmail())
                    .password(user.getPassword())
                    .department(user.getDepartment())
                    .verified(user.isVerified())
                    .role(user.getRole())
                    .providerType(user.getProviderType())
                    .providerId(user.getProviderId())
                    .build();
        } else {
            // 신규 사용자면 신규 등록
            user = attributes.toEntity();
        }

        return userRepository.save(user);
    }
}