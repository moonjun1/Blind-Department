package com.campus.campuscommunity.domain.user.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name="users")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id // 기본키(Primary Key) 지정
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 자동 증가 값 사용
    private Long id;

    @Column(nullable = false, unique = true) // null 허용 안함, 중복 값 허용 안함
    private String email;

    @Column(nullable = true)
    private String password;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String department; // 학과

    private boolean verified; // 학생증 인증 여부

    // 소셜 로그인 관련 필드 추가
    @Enumerated(EnumType.STRING)
    private ProviderType providerType; // 로그인 제공자 (GOOGLE, LOCAL 등)

    private String providerId; // 제공자 ID

    // 권한 추가
    @Enumerated(EnumType.STRING)
    private Role role;

    // 로그인 제공자 타입 열거형
    public enum ProviderType {
        LOCAL, GOOGLE
    }

    // 사용자 권한 열거형
    public enum Role {
        USER, ADMIN
    }

    // 권한 반환 메서드
    public String getRoleKey() {
        return this.role.name();
    }
}