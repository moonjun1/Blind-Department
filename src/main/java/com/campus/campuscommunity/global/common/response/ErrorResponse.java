package com.campus.campuscommunity.global.common.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.validation.BindingResult;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {

    private int status;                 // HTTP 상태 코드
    private String message;             // 오류 메시지
    private List<FieldError> errors;    // 필드 오류 목록

    // ResponseCode로 ErrorResponse 생성
    public static ErrorResponse of(ResponseCode code) {
        return ErrorResponse.builder()
                .status(code.getStatus())
                .message(code.getMessage())
                .build();
    }

    // ResponseCode와 커스텀 메시지로 ErrorResponse 생성
    public static ErrorResponse of(ResponseCode code, String message) {
        return ErrorResponse.builder()
                .status(code.getStatus())
                .message(message)
                .build();
    }

    // 유효성 검사 오류 처리
    public static ErrorResponse of(ResponseCode code, BindingResult bindingResult) {
        return ErrorResponse.builder()
                .status(code.getStatus())
                .message(code.getMessage())
                .errors(FieldError.of(bindingResult))
                .build();
    }

    // 필드 오류 클래스
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FieldError {
        private String field;       // 오류 필드명
        private String value;       // 오류 값
        private String reason;      // 오류 이유

        // BindingResult에서 FieldError 목록 생성
        public static List<FieldError> of(BindingResult bindingResult) {
            List<org.springframework.validation.FieldError> fieldErrors = bindingResult.getFieldErrors();
            return fieldErrors.stream()
                    .map(error -> new FieldError(
                            error.getField(),
                            error.getRejectedValue() == null ? "" : error.getRejectedValue().toString(),
                            error.getDefaultMessage()))
                    .collect(Collectors.toList());
        }
    }
}