package com.campus.campuscommunity.domain.user.dto;

import com.campus.campuscommunity.domain.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
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
    @Schema(description = "사용자 정보 응답 DTO")
    public static class UserInfo {

        @Schema(description = "사용자 ID", example = "1")
        private Long id;

        @Schema(description = "사용자 이메일", example = "user@university.ac.kr")
        private String email;

        @Schema(description = "사용자 이름", example = "홍길동")
        private String name;

        @Schema(description = "학과명", example = "컴퓨터공학과")
        private String department;

        @Schema(description = "학과 인증 여부", example = "true")
        private boolean verified;

        @Schema(description = "인증 상태 메시지", example = "학과 인증이 완료되었습니다.")
        private String verificationStatus;

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
    @Schema(description = "로그인 응답 DTO")
    public static class LoginResponse {
        @Schema(description = "JWT 토큰", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ1c2VyQHVuaXZlcnNpdHkuYWMua3IiLCJpYXQiOjE2MTYxMjM4MDB9.example_token_signature")
        private String token;

        @Schema(description = "사용자 정보")
        private UserInfo userInfo;
    }
}