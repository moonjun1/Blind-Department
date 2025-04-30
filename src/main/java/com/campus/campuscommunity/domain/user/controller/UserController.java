package com.campus.campuscommunity.domain.user.controller;

import com.campus.campuscommunity.global.common.response.ApiResponse;
import com.campus.campuscommunity.global.common.response.ResponseCode;
import com.campus.campuscommunity.domain.user.dto.UserRequestDto;
import com.campus.campuscommunity.domain.user.dto.UserResponseDto;
import com.campus.campuscommunity.domain.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "사용자 관리", description = "회원가입, 로그인, 사용자 정보 관리 API")
public class UserController {

    private final UserService userService;

    /**
     * 회원가입 API
     * POST /api/users/signup
     */
    @Operation(
            summary = "회원가입",
            description = "새로운 사용자를 등록합니다. 이메일, 비밀번호, 이름, 학과 정보가 필요합니다."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "회원가입 성공",
                    content = @Content(schema = @Schema(implementation = UserResponseDto.UserInfo.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 (입력값 오류)"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "409",
                    description = "이메일 중복"
            )
    })
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
    @Operation(
            summary = "로그인",
            description = "이메일과 비밀번호로 로그인합니다. 성공 시 JWT 토큰을 반환합니다."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "로그인 성공",
                    content = @Content(schema = @Schema(implementation = UserResponseDto.LoginResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "잘못된 비밀번호"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "사용자 없음"
            )
    })
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
    @Operation(
            summary = "사용자 정보 조회",
            description = "현재 로그인한 사용자의 정보를 조회합니다."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "정보 조회 성공",
                    content = @Content(schema = @Schema(implementation = UserResponseDto.UserInfo.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "사용자 없음"
            )
    })
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponseDto.UserInfo>> getMyInfo(
            @Parameter(description = "사용자 이메일", example = "student@university.ac.kr", required = true)
            @RequestParam String email) {

        UserResponseDto.UserInfo userInfo = userService.getUserInfo(email);
        return ResponseEntity.ok(ApiResponse.success(userInfo));
    }

    /**
     * 사용자 정보 수정 API
     * PUT /api/users/me
     */
    @Operation(
            summary = "사용자 정보 수정",
            description = "현재 로그인한 사용자의 이름과 학과 정보를 수정합니다."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "정보 수정 성공",
                    content = @Content(schema = @Schema(implementation = UserResponseDto.UserInfo.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 (입력값 오류)"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "사용자 없음"
            )
    })
    @PutMapping("/me")
    public ResponseEntity<ApiResponse<UserResponseDto.UserInfo>> updateMyInfo(
            @Parameter(description = "사용자 이메일", example = "student@university.ac.kr", required = true)
            @RequestParam String email,
            @Valid @RequestBody UserRequestDto.UpdateRequest request) {

        UserResponseDto.UserInfo userInfo = userService.updateUserInfo(email, request);
        return ResponseEntity.ok(ApiResponse.success("회원정보가 성공적으로 수정되었습니다.", userInfo));
    }

    /**
     * 학과 인증 API (OCR 로직은 나중에 구현)
     * POST /api/users/verify-department
     */
    @Operation(
            summary = "학생증 OCR 인증",
            description = "학생증 이미지를 OCR로 분석하여 학과를 인증합니다."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "학과 인증 성공",
                    content = @Content(schema = @Schema(implementation = UserResponseDto.UserInfo.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 (이미지 오류)"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "사용자 없음"
            )
    })
    @PostMapping(value = "/verify-department/ocr", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<UserResponseDto.UserInfo>> verifyDepartmentWithOcr(
            @Parameter(description = "사용자 이메일", example = "student@university.ac.kr", required = true)
            @RequestParam("email") String email,
            @Parameter(description = "학생증 이미지 파일", required = true)
            @RequestParam("studentIdCard") MultipartFile studentIdCard) {

        try {
            UserResponseDto.UserInfo userInfo = userService.verifyDepartmentWithOcr(email, studentIdCard);
            return ResponseEntity.ok(ApiResponse.success("학과 인증이 완료되었습니다.", userInfo));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(ResponseCode.BAD_REQUEST, e.getMessage()));
        }
    }

    /**
     * OAuth2 로그인 사용자 정보 업데이트 API
     * PUT /api/users/oauth/info
     */
    @Operation(
            summary = "OAuth 사용자 정보 업데이트",
            description = "OAuth 로그인 사용자의 학과 정보를 업데이트합니다."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "정보 업데이트 성공",
                    content = @Content(schema = @Schema(implementation = UserResponseDto.UserInfo.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "사용자 없음"
            )
    })
    @PutMapping("/oauth/info")
    public ResponseEntity<ApiResponse<UserResponseDto.UserInfo>> updateOAuthUserInfo(
            @Parameter(description = "사용자 이메일", example = "user@gmail.com", required = true)
            @RequestParam String email,
            @Parameter(description = "학과명", example = "컴퓨터공학과", required = true)
            @RequestParam String department) {

        UserResponseDto.UserInfo userInfo = userService.updateOAuthUserInfo(email, department);
        return ResponseEntity.ok(ApiResponse.success("학과 정보가 성공적으로 업데이트되었습니다.", userInfo));
    }
}