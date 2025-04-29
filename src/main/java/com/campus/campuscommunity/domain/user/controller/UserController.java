package com.campus.campuscommunity.domain.user.controller;

import com.campus.campuscommunity.common.response.ApiResponse;
import com.campus.campuscommunity.common.response.ResponseCode;
import com.campus.campuscommunity.domain.user.dto.UserRequestDto;
import com.campus.campuscommunity.domain.user.dto.UserResponseDto;
import com.campus.campuscommunity.domain.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 회원가입 API
     * POST /api/users/signup
     */
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<UserResponseDto.UserInfo>> signUp(
            @Valid @RequestBody UserRequestDto.SignUpRequest request) {

        UserResponseDto.UserInfo userInfo = userService.signUp(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("회원가입이 성공적으로 완료되었습니다.", userInfo));
    }

    /**
     * 로그인 API
     * POST /api/users/login
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<UserResponseDto.LoginResponse>> login(
            @Valid @RequestBody UserRequestDto.LoginRequest request) {

        UserResponseDto.LoginResponse response = userService.login(request);
        return ResponseEntity.ok(ApiResponse.success("로그인에 성공했습니다.", response));
    }

    /**
     * 사용자 정보 조회 API
     * GET /api/users/me
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponseDto.UserInfo>> getMyInfo(
            @RequestParam String email) {

        UserResponseDto.UserInfo userInfo = userService.getUserInfo(email);
        return ResponseEntity.ok(ApiResponse.success(userInfo));
    }

    /**
     * 사용자 정보 수정 API
     * PUT /api/users/me
     */
    @PutMapping("/me")
    public ResponseEntity<ApiResponse<UserResponseDto.UserInfo>> updateMyInfo(
            @RequestParam String email,
            @Valid @RequestBody UserRequestDto.UpdateRequest request) {

        UserResponseDto.UserInfo userInfo = userService.updateUserInfo(email, request);
        return ResponseEntity.ok(ApiResponse.success("회원정보가 성공적으로 수정되었습니다.", userInfo));
    }

    /**
     * 학과 인증 API (OCR 로직은 나중에 구현)
     * POST /api/users/verify-department
     */
    @PostMapping("/verify-department/ocr")
    public ResponseEntity<ApiResponse<UserResponseDto.UserInfo>> verifyDepartmentWithOcr(
            @RequestParam("email") String email,
            @RequestParam("studentIdCard") MultipartFile studentIdCard) {

        try {
            UserResponseDto.UserInfo userInfo = userService.verifyDepartmentWithOcr(email, studentIdCard);
            return ResponseEntity.ok(ApiResponse.success("학과 인증이 완료되었습니다.", userInfo));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(ResponseCode.BAD_REQUEST, e.getMessage()));
        }
    }
}