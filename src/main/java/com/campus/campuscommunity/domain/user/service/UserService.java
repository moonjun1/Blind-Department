package com.campus.campuscommunity.domain.user.service;

import com.campus.campuscommunity.global.common.response.ResponseCode;
import com.campus.campuscommunity.global.config.exception.CustomException;
import com.campus.campuscommunity.global.config.jwt.JwtTokenProvider;
import com.campus.campuscommunity.domain.user.dto.UserRequestDto;
import com.campus.campuscommunity.domain.user.dto.UserResponseDto;
import com.campus.campuscommunity.domain.user.entity.User;
import com.campus.campuscommunity.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j // 로깅 기능 추가
@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final StudentIdVerificationService verificationService;

    /**
     * 회원가입 처리
     * @param request 회원가입 요청 정보
     * @return 가입된 사용자 정보
     */
    public UserResponseDto.UserInfo signUp(UserRequestDto.SignUpRequest request) {
        log.info("회원가입 시작: 이메일={}, 이름={}, 학과={}", request.getEmail(), request.getName(), request.getDepartment());

        // 이메일 중복 확인
        if (userRepository.existsByEmail(request.getEmail())) {
            log.warn("회원가입 실패: 이메일 중복 - {}", request.getEmail());
            throw new CustomException(ResponseCode.EMAIL_DUPLICATION);
        }

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(request.getPassword());
        log.debug("비밀번호 암호화 완료");

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
        log.info("회원가입 완료: ID={}, 이메일={}", savedUser.getId(), savedUser.getEmail());

        // 응답 DTO 변환 후 반환
        return UserResponseDto.UserInfo.from(savedUser);
    }

    /**
     * 로그인 처리
     * @param request 로그인 요청 정보
     * @return 로그인 응답 (토큰 + 사용자 정보)
     */
    public UserResponseDto.LoginResponse login(UserRequestDto.LoginRequest request) {
        log.info("로그인 시도: 이메일={}", request.getEmail());

        // 이메일로 사용자 조회
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> {
                    log.warn("로그인 실패: 사용자를 찾을 수 없음 - {}", request.getEmail());
                    return new CustomException(ResponseCode.USER_NOT_FOUND);
                });

        // 비밀번호 확인
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            log.warn("로그인 실패: 비밀번호 불일치 - 이메일={}", request.getEmail());
            throw new CustomException(ResponseCode.INVALID_PASSWORD);
        }

        // JWT 토큰 생성
        String token = jwtTokenProvider.createToken(user.getEmail());
        log.info("로그인 성공: 이메일={}", user.getEmail());

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
    @Transactional(readOnly = true)
    public UserResponseDto.UserInfo getUserInfo(String email) {
        log.debug("사용자 정보 조회: 이메일={}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("사용자 정보 조회 실패: 사용자를 찾을 수 없음 - {}", email);
                    return new CustomException(ResponseCode.USER_NOT_FOUND);
                });

        log.debug("사용자 정보 조회 성공: ID={}, 이름={}, 학과={}", user.getId(), user.getName(), user.getDepartment());
        return UserResponseDto.UserInfo.from(user);
    }

    /**
     * 사용자 정보 수정
     * @param email 이메일
     * @param request 수정 요청 정보
     * @return 수정된 사용자 정보
     */
    public UserResponseDto.UserInfo updateUserInfo(String email, UserRequestDto.UpdateRequest request) {
        log.info("사용자 정보 수정 시작: 이메일={}, 이름={}, 학과={}", email, request.getName(), request.getDepartment());

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("사용자 정보 수정 실패: 사용자를 찾을 수 없음 - {}", email);
                    return new CustomException(ResponseCode.USER_NOT_FOUND);
                });

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
        log.info("사용자 정보 수정 완료: ID={}, 이름={}, 학과={}", savedUser.getId(), savedUser.getName(), savedUser.getDepartment());

        return UserResponseDto.UserInfo.from(savedUser);
    }

    /**
     * 비밀번호 변경
     * @param email 이메일
     * @param request 비밀번호 변경 요청
     * @return 변경된 사용자 정보
     */
    public UserResponseDto.UserInfo changePassword(String email, UserRequestDto.PasswordChangeRequest request) {
        log.info("비밀번호 변경 시도: 이메일={}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("비밀번호 변경 실패: 사용자를 찾을 수 없음 - {}", email);
                    return new CustomException(ResponseCode.USER_NOT_FOUND);
                });

        // 현재 비밀번호 확인
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            log.warn("비밀번호 변경 실패: 현재 비밀번호 불일치 - 이메일={}", email);
            throw new CustomException(ResponseCode.INVALID_PASSWORD);
        }

        // 새 비밀번호 암호화
        String newEncodedPassword = passwordEncoder.encode(request.getNewPassword());
        log.debug("새 비밀번호 암호화 완료");

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
        log.info("비밀번호 변경 완료: ID={}, 이메일={}", savedUser.getId(), savedUser.getEmail());

        return UserResponseDto.UserInfo.from(savedUser);
    }

    /**
     * 학과 인증 처리 (OCR 로직은 나중에 구현)
     * @param email 이메일
     * @return 인증된 사용자 정보
     */
    public UserResponseDto.UserInfo verifyDepartmentWithOcr(String email, MultipartFile studentIdCard)
            throws IOException {
        log.info("학생증 OCR 인증 시작: 이메일={}, 파일명={}", email, studentIdCard.getOriginalFilename());

        // 이메일로 사용자 조회
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("학생증 인증 실패: 사용자를 찾을 수 없음 - {}", email);
                    return new CustomException(ResponseCode.USER_NOT_FOUND);
                });

        try {
            // OCR로 학생증 인증 및 학과 추출
            String detectedDepartment = verificationService.verifyStudentIdCard(studentIdCard);
            log.info("OCR 학과 인식 결과: {}", detectedDepartment);

            // 인증 상태 업데이트
            User verifiedUser = User.builder()
                    .id(user.getId())
                    .email(user.getEmail())
                    .password(user.getPassword())
                    .name(user.getName())
                    .department(user.getDepartment())
                    .verified(true) // 인증 상태로 변경
                    .build();

            User savedUser = userRepository.save(verifiedUser);
            log.info("학생증 인증 완료: ID={}, 이메일={}", savedUser.getId(), savedUser.getEmail());

            return UserResponseDto.UserInfo.from(savedUser);
        } catch (Exception e) {
            log.error("학생증 OCR 인증 오류: 이메일={}, 오류={}", email, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 회원 탈퇴
     * @param email 이메일
     */
    public void deleteUser(String email) {
        log.info("회원 탈퇴 시작: 이메일={}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("회원 탈퇴 실패: 사용자를 찾을 수 없음 - {}", email);
                    return new CustomException(ResponseCode.USER_NOT_FOUND);
                });

        userRepository.delete(user);
        log.info("회원 탈퇴 완료: ID={}, 이메일={}", user.getId(), user.getEmail());
    }

    /**
     * OAuth2 로그인 사용자의 추가 정보 업데이트 (학과 정보 등)
     * @param email 사용자 이메일
     * @param department 학과 정보
     * @return 업데이트된 사용자 정보
     */
    @Transactional
    public UserResponseDto.UserInfo updateOAuthUserInfo(String email, String department) {
        log.info("OAuth 사용자 정보 업데이트: 이메일={}, 학과={}", email, department);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("OAuth 사용자 정보 업데이트 실패: 사용자를 찾을 수 없음 - {}", email);
                    return new CustomException(ResponseCode.USER_NOT_FOUND);
                });

        User updatedUser = User.builder()
                .id(user.getId())
                .email(user.getEmail())
                .password(user.getPassword())
                .name(user.getName())
                .department(department)
                .verified(false) // OAuth 사용자도 학생증 인증 필요
                .role(user.getRole())
                .providerType(user.getProviderType())
                .providerId(user.getProviderId())
                .build();

        User savedUser = userRepository.save(updatedUser);
        log.info("OAuth 사용자 정보 업데이트 완료: ID={}, 이메일={}, 학과={}",
                savedUser.getId(), savedUser.getEmail(), savedUser.getDepartment());

        return UserResponseDto.UserInfo.from(savedUser);
    }
}