package com.campus.campuscommunity.domain.user.dto;

import com.campus.campuscommunity.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 응답 데이터를 담는 클래스들
public class UserResponseDto {

    // 사용자 정보 응답 DTO
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserInfo {
        private Long id;
        private String email;
        private String name;
        private String department;
        private boolean verified;

        // User 엔티티로부터 UserInfo DTO 생성하는 정적 메서드
        public static UserInfo from(User user) {
            return UserInfo.builder()
                    .id(user.getId())
                    .email(user.getEmail())
                    .name(user.getName())
                    .department(user.getDepartment())
                    .verified(user.isVerified())
                    .build();
        }
    }

    // 로그인 응답 DTO
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoginResponse {
        private String token;  // JWT 토큰
        private UserInfo userInfo;  // 사용자 정보
    }
}