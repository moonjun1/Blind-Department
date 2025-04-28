package com.campus.campuscommunity.domain.user.service;

import com.campus.campuscommunity.common.response.ResponseCode;
import com.campus.campuscommunity.config.exception.CustomException;
import com.campus.campuscommunity.config.jwt.JwtTokenProvider;
import com.campus.campuscommunity.domain.user.dto.UserRequestDto;
import com.campus.campuscommunity.domain.user.dto.UserResponseDto;
import com.campus.campuscommunity.domain.user.entity.User;
import com.campus.campuscommunity.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service // 이 클래스가 서비스 계층임을 나타냅니다
@RequiredArgsConstructor // final 필드에 대한 생성자를 자동 생성합니다
@Transactional // 모든 메서드가 트랜잭션 내에서 실행되도록 합니다
public class UserService {

    private final UserRepository userRepository; // 사용자 저장소
    private final PasswordEncoder passwordEncoder; // 비밀번호 암호화 도구
    private final JwtTokenProvider jwtTokenProvider; // JWT 토큰 생성 도구

    /**
     * 회원가입 처리
     * @param request 회원가입 요청 정보
     * @return 가입된 사용자 정보
     */
    public UserResponseDto.UserInfo signUp(UserRequestDto.SignUpRequest request) {
        // 이메일 중복 확인
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new CustomException(ResponseCode.EMAIL_DUPLICATION);
        }

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        // 사용자 생성
        User user = User.builder()
                .email(request.getEmail())
                .password(encodedPassword)
                .name(request.getName())
                .department(request.getDepartment())
                .verified(false) // 초기에는 미인증 상태
                .build();

        // 사용자 저장
        User savedUser = userRepository.save(user);

        // 응답 DTO 변환 후 반환
        return UserResponseDto.UserInfo.from(savedUser);
    }

    /**
     * 로그인 처리
     * @param request 로그인 요청 정보
     * @return 로그인 응답 (토큰 + 사용자 정보)
     */
    public UserResponseDto.LoginResponse login(UserRequestDto.LoginRequest request) {
        // 이메일로 사용자 조회
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new CustomException(ResponseCode.USER_NOT_FOUND));

        // 비밀번호 확인
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new CustomException(ResponseCode.INVALID_PASSWORD);
        }

        // JWT 토큰 생성
        String token = jwtTokenProvider.createToken(user.getEmail());

        // 응답 생성
        return UserResponseDto.LoginResponse.builder()
                .token(token)
                .userInfo(UserResponseDto.UserInfo.from(user))
                .build();
    }

    /**
     * 사용자 정보 조회
     * @param email 이메일
     * @return 사용자 정보
     */
    @Transactional(readOnly = true) // 읽기 전용 트랜잭션 (성능 최적화)
    public UserResponseDto.UserInfo getUserInfo(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ResponseCode.USER_NOT_FOUND));

        return UserResponseDto.UserInfo.from(user);
    }

    /**
     * 사용자 정보 수정
     * @param email 이메일
     * @param request 수정 요청 정보
     * @return 수정된 사용자 정보
     */
    public UserResponseDto.UserInfo updateUserInfo(String email, UserRequestDto.UpdateRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ResponseCode.USER_NOT_FOUND));

        // 정보 업데이트
        User updatedUser = User.builder()
                .id(user.getId())
                .email(user.getEmail())
                .password(user.getPassword())
                .name(request.getName())
                .department(request.getDepartment())
                .verified(user.isVerified())
                .build();

        User savedUser = userRepository.save(updatedUser);

        return UserResponseDto.UserInfo.from(savedUser);
    }

    /**
     * 비밀번호 변경
     * @param email 이메일
     * @param request 비밀번호 변경 요청
     * @return 변경된 사용자 정보
     */
    public UserResponseDto.UserInfo changePassword(String email, UserRequestDto.PasswordChangeRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ResponseCode.USER_NOT_FOUND));

        // 현재 비밀번호 확인
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new CustomException(ResponseCode.INVALID_PASSWORD);
        }

        // 새 비밀번호 암호화
        String newEncodedPassword = passwordEncoder.encode(request.getNewPassword());

        // 비밀번호 업데이트
        User updatedUser = User.builder()
                .id(user.getId())
                .email(user.getEmail())
                .password(newEncodedPassword)
                .name(user.getName())
                .department(user.getDepartment())
                .verified(user.isVerified())
                .build();

        User savedUser = userRepository.save(updatedUser);

        return UserResponseDto.UserInfo.from(savedUser);
    }

    /**
     * 학과 인증 처리 (OCR 로직은 나중에 구현)
     * @param email 이메일
     * @return 인증된 사용자 정보
     */
    public UserResponseDto.UserInfo verifyDepartment(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ResponseCode.USER_NOT_FOUND));

        // 인증 상태로 변경
        User verifiedUser = User.builder()
                .id(user.getId())
                .email(user.getEmail())
                .password(user.getPassword())
                .name(user.getName())
                .department(user.getDepartment())
                .verified(true) // 인증 상태로 변경
                .build();

        User savedUser = userRepository.save(verifiedUser);

        return UserResponseDto.UserInfo.from(savedUser);
    }

    /**
     * 회원 탈퇴
     * @param email 이메일
     */
    public void deleteUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ResponseCode.USER_NOT_FOUND));

        userRepository.delete(user);
    }
}