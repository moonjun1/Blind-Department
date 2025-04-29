package com.campus.campuscommunity.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class UserRequestDto {

    // 회원가입 요청 DTO
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "회원가입 요청 DTO")
    public static class SignUpRequest {

        @Schema(description = "사용자 이메일", example = "user@university.ac.kr", required = true)
        @NotBlank(message = "이메일은 필수 입력값입니다.")
        @Email(message = "유효한 이메일 형식이 아닙니다.")
        private String email;

        @Schema(description = "비밀번호 (8자 이상, 영문자+숫자 조합)", example = "password123", required = true)
        @NotBlank(message = "비밀번호는 필수 입력값입니다.")
        @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다.")
        @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$",
                message = "비밀번호는 영문자와 숫자를 포함해야 합니다.")
        private String password;

        @Schema(description = "사용자 이름", example = "홍길동", required = true)
        @NotBlank(message = "이름은 필수 입력값입니다.")
        private String name;

        @Schema(description = "학과명", example = "컴퓨터공학과", required = true)
        @NotBlank(message = "학과는 필수 입력값입니다.")
        private String department;
    }

    // 로그인 요청 DTO
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "로그인 요청 DTO")
    public static class LoginRequest {

        @Schema(description = "사용자 이메일", example = "user@university.ac.kr", required = true)
        @NotBlank(message = "이메일은 필수 입력값입니다.")
        @Email(message = "유효한 이메일 형식이 아닙니다.")
        private String email;

        @Schema(description = "비밀번호", example = "password123", required = true)
        @NotBlank(message = "비밀번호는 필수 입력값입니다.")
        private String password;
    }

    // 회원 정보 수정 요청 DTO
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "회원 정보 수정 요청 DTO")
    public static class UpdateRequest {

        @Schema(description = "변경할 이름", example = "김철수", required = true)
        @NotBlank(message = "이름은 필수 입력값입니다.")
        private String name;

        @Schema(description = "변경할 학과명", example = "전자공학과", required = true)
        @NotBlank(message = "학과는 필수 입력값입니다.")
        private String department;
    }

    // 비밀번호 변경 요청 DTO
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "비밀번호 변경 요청 DTO")
    public static class PasswordChangeRequest {

        @Schema(description = "현재 비밀번호", example = "oldpassword123", required = true)
        @NotBlank(message = "현재 비밀번호는 필수 입력값입니다.")
        private String currentPassword;

        @Schema(description = "새 비밀번호 (8자 이상, 영문자+숫자 조합)", example = "newpassword456", required = true)
        @NotBlank(message = "새 비밀번호는 필수 입력값입니다.")
        @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다.")
        @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$",
                message = "비밀번호는 영문자와 숫자를 포함해야 합니다.")
        private String newPassword;
    }
}