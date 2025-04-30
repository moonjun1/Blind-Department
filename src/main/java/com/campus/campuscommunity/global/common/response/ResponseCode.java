package com.campus.campuscommunity.global.common.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ResponseCode {

    // 성공
    SUCCESS(200, "요청이 성공적으로 처리되었습니다."),

    // 클라이언트 오류 (400번대)
    BAD_REQUEST(400, "잘못된 요청입니다."),
    UNAUTHORIZED(401, "인증이 필요합니다."),
    FORBIDDEN(403, "접근 권한이 없습니다."),
    NOT_FOUND(404, "요청한 리소스를 찾을 수 없습니다."),

    // 회원 관련 오류
    EMAIL_DUPLICATION(409, "이미 가입된 이메일입니다."),
    USER_NOT_FOUND(404, "사용자를 찾을 수 없습니다."),
    INVALID_PASSWORD(400, "비밀번호가 일치하지 않습니다."),

    // 서버 오류 (500번대)
    SERVER_ERROR(500, "서버 내부 오류가 발생했습니다.");

    private final int status;
    private final String message;
}