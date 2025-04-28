package com.campus.campuscommunity.domain.user.repository;

import com.campus.campuscommunity.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository // 이 인터페이스가 Repository 역할을 함을 나타냅니다
public interface UserRepository extends JpaRepository<User, Long> {

    // 이메일로 사용자 찾기
    // 메서드 이름만으로 SQL 쿼리가 자동 생성됩니다 (SELECT * FROM users WHERE email = ?)
    Optional<User> findByEmail(String email);

    // 이메일 존재 여부 확인
    // 메서드 이름만으로 SQL 쿼리가 자동 생성됩니다 (SELECT EXISTS(SELECT 1 FROM users WHERE email = ?))
    boolean existsByEmail(String email);
}